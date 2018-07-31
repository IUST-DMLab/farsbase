package ir.ac.iust.dml.kg.knowledge.store.services.filter;

import ir.ac.iust.dml.kg.raw.utils.URIs;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        final String origin = request.getHeader("Origin");
        if (origin != null) {
            if (origin.startsWith("http://dmls.iust.ac.ir") ||
                    origin.startsWith("http://fkg.iust.ac.ir") ||
                origin.startsWith(URIs.INSTANCE.getFkgMainPrefixUrl()) ||
                    origin.startsWith("http://194.225.227.161")) {
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}

