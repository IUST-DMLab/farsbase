package ir.ac.iust.dml.kg.knowledge.proxy.web.controller;

import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.UrnMatching;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.proxy.web.logic.ForwardLogic;
import ir.ac.iust.dml.kg.knowledge.proxy.web.security.MyUserDetails;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

@Controller
public class ProxyController {
    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
     * approach does case insensitive lookup faster.
     */
    private static final HeaderGroup hopByHopHeaders;
    private final ForwardLogic logic;

    static {
        hopByHopHeaders = new HeaderGroup();
        String[] headers = new String[]{
                "x-auth-username", "x-auth-identifier", "x-auth-permissions",
                "Keep-Alive",
                "Access-Control-Allow-Origin",
                HttpHeaders.HOST, HttpHeaders.CONNECTION, HttpHeaders.CONTENT_LENGTH,
                HttpHeaders.PROXY_AUTHENTICATE, HttpHeaders.PROXY_AUTHORIZATION,
                HttpHeaders.TE, HttpHeaders.TRAILER, HttpHeaders.TRANSFER_ENCODING, HttpHeaders.UPGRADE};
        for (String header : headers) {
            hopByHopHeaders.addHeader(new BasicHeader(header, null));
        }
    }

    private final HttpClient client;

    @Autowired
    public ProxyController(ForwardLogic logic) {
        this.logic = logic;
        final RequestConfig config = RequestConfig.custom()
                .setRedirectsEnabled(false)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES) // we handle them in the servlet instead
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .build();
        client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config).build();
    }


    @RequestMapping("/proxy/{source}/**")
    public void proxy(
            @PathVariable("source") String source,
            HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        final Forward forward = logic.get(source);
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final User user = principal != null && principal instanceof MyUserDetails ? ((MyUserDetails) principal).getUser() : null;

        final String urn = urnOfRequest(request, source);
        if (!hasPermission(forward, user, urn, request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final URI destination = new URI(forward.getDestination());
        final String proxyRequestUri = rewriteUrlFromRequest(urn, destination);
        final HttpRequest proxyRequest = createProxyRequest(request, proxyRequestUri);
        copyRequestHeaders(request, proxyRequest);
        setForwardedHeader(request, proxyRequest);
        setForwardedAuthenticaton(user, proxyRequest);
        final HttpResponse proxyResponse = client.execute(URIUtils.extractHost(destination), proxyRequest);
        // Pass the response code. This method with the "reason phrase" is deprecated but it's the only way to pass the reason along too.
        final int statusCode = proxyResponse.getStatusLine().getStatusCode();
        //noinspection deprecation
        response.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());
        // Copying response headers to make sure SESSIONID or other Cookie which comes from the remote
        // server will be saved in client when the proxied url was redirected to another one.
        // See issue [#51](https://github.com/mitre/HTTP-Proxy-Servlet/issues/51)
        copyResponseHeaders(proxyResponse, request, response);
        if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
            // 304 needs special handling.  See:
            // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
            // Don't send body entity/content!
            response.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
        } else {
            // Send the content to the client
            copyResponseEntity(proxyResponse, response);
        }

    }

    private boolean hasPermission(Forward forward, User user, String urn, String method) {
        final UrnMatching checkUrn = forward.match(urn, method.toUpperCase());
//        if(checkUrn != null)
//            System.out.println(checkUrn.getUrn());
        final Set<Permission> permissions = checkUrn != null ? checkUrn.getPermissions() : forward.getPermissions();
//        permissions.forEach(it -> System.out.println(it.getTitle()));
//        if(user != null)
//            user.getPermissions().forEach(it -> System.out.println(it.getTitle()));
        if (!permissions.isEmpty()) {
            boolean found = false;
            if (user != null)
                for (Permission fp : permissions)
                    for (Permission p : user.getPermissions())
                        if (fp.getTitle().equals(p.getTitle()))
                            found = true;
            return found;
        }
        return true;
    }

    private String urnOfRequest(HttpServletRequest request, String source) {
        final StringBuilder url = new StringBuilder();
        url.append(request.getServletPath().substring(request.getServletPath().indexOf("/proxy/" + source) + ("/proxy/" + source).length()));
        if (request.getQueryString() != null)
            url.append("?").append(request.getQueryString());
        return url.toString();
    }

    private String rewriteUrlFromRequest(String urn, URI destination) {
        final StringBuilder url = new StringBuilder();
        if (destination.getPath().endsWith("/"))
            url.append(destination.getPath().substring(0, destination.getPath().length() - 1));
        else
            url.append(destination.getPath());
        url.append(urn);
        return url.toString();
    }


    private HttpRequest createProxyRequest(HttpServletRequest request, String proxyRequestUri) throws IOException {
        final String method = request.getMethod();
        //spec: RFC 2616, sec 4.3: either of these two headers signal that there is a message body.
        if (request.getHeader(HttpHeaders.CONTENT_LENGTH) != null ||
                request.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            final HttpEntityEnclosingRequest proxyRequest = new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
            // Add the input entity (streamed)
            //  note: we don't bother ensuring we close the servletInputStream since the container handles it
            final String contentLengthHeader = request.getHeader(HttpHeaders.CONTENT_LENGTH);
            proxyRequest.setEntity(
                    new InputStreamEntity(request.getInputStream(),
                            contentLengthHeader != null ? Long.parseLong(contentLengthHeader) : -1L));
            return proxyRequest;
        } else {
            return new BasicHttpRequest(method, proxyRequestUri);
        }
    }

    private void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
        // Get an Enumeration of all of the header names sent by the client
        Enumeration<String> enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            final String headerName = enumerationOfHeaderNames.nextElement();
            if (hopByHopHeaders.containsHeader(headerName))
                continue;
            Enumeration<String> headers = servletRequest.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                final String headerValue = headers.nextElement();
                proxyRequest.addHeader(headerName, headerValue);
            }

        }
    }

    private void setForwardedHeader(HttpServletRequest servletRequest,
                                    HttpRequest proxyRequest) {
        String forHeaderName = "X-Forwarded-For";
        String forHeader = servletRequest.getRemoteAddr();
        String existingForHeader = servletRequest.getHeader(forHeaderName);
        if (existingForHeader != null) {
            forHeader = existingForHeader + ", " + forHeader;
        }
        proxyRequest.setHeader(forHeaderName, forHeader);

        String protoHeaderName = "X-Forwarded-Proto";
        String protoHeader = servletRequest.getScheme();
        proxyRequest.setHeader(protoHeaderName, protoHeader);
    }

    private void setForwardedAuthenticaton(User user,
                                           HttpRequest proxyRequest) {
        if (user == null) return;
        proxyRequest.setHeader("x-auth-username", user.getUsername());
        proxyRequest.setHeader("x-auth-identifier", user.getIdentifier());
        user.getPermissions().forEach(p -> proxyRequest.addHeader("x-auth-permissions", p.getTitle()));
    }

    /**
     * Copy proxied response headers back to the servlet client.
     */
    private void copyResponseHeaders(HttpResponse proxyResponse, HttpServletRequest servletRequest,
                                     HttpServletResponse servletResponse) {
        for (Header header : proxyResponse.getAllHeaders()) {
            copyResponseHeader(servletRequest, servletResponse, header);
        }
    }

    /**
     * Copy a proxied response header back to the servlet client.
     * This is easily overwritten to filter out certain headers if desired.
     */
    private void copyResponseHeader(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse, Header header) {
        final String headerName = header.getName();
        if (hopByHopHeaders.containsHeader(headerName))
            return;
        final String headerValue = header.getValue();
        if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE) ||
                headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
            copyProxyCookie(servletRequest, servletResponse, headerValue);
        } else {
            servletResponse.addHeader(headerName, headerValue);
        }
    }

    /**
     * Copy cookie from the proxy to the servlet client.
     * Replaces cookie path to local path and renames cookie to avoid collisions.
     */
    private void copyProxyCookie(HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse, String headerValue) {
        List<HttpCookie> cookies = HttpCookie.parse(headerValue);
        String path = servletRequest.getContextPath(); // path starts with / or is empty string
        path += servletRequest.getServletPath(); // servlet path starts with / or is empty string
        if (path.isEmpty()) {
            path = "/";
        }

        for (HttpCookie cookie : cookies) {
            //set cookie name prefixed w/ a proxy value so it won't collide w/ other cookies
            String proxyCookieName = cookie.getName();
            Cookie servletCookie = new Cookie(proxyCookieName, cookie.getValue());
            servletCookie.setComment(cookie.getComment());
            servletCookie.setMaxAge((int) cookie.getMaxAge());
            servletCookie.setPath(path); //set to the path of the proxy servlet
            // don't set cookie domain
            servletCookie.setSecure(cookie.getSecure());
            servletCookie.setVersion(cookie.getVersion());
            servletResponse.addCookie(servletCookie);
        }
    }

    /**
     * Copy response body data (the entity) from the proxy to the servlet client.
     */
    private void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse)
            throws IOException {
        HttpEntity entity = proxyResponse.getEntity();
        if (entity != null) {
            OutputStream servletOutputStream = servletResponse.getOutputStream();
            entity.writeTo(servletOutputStream);
        }
    }
}
