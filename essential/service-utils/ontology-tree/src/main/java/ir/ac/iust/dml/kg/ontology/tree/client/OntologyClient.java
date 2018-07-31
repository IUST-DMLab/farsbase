package ir.ac.iust.dml.kg.ontology.tree.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

public class OntologyClient {
  private final String address;
  private final HttpClientBuilder builder = HttpClientBuilder.create();
  private final Type pagedType = new TypeToken<PagedData<OntologyClass>>() {
  }.getType();
  private final Gson gson = new Gson();


  public OntologyClient(String address) {
    this.address = address;
    builder.setDefaultRequestConfig(RequestConfig.DEFAULT);
  }

  public PagedData<OntologyClass> search(int page, int pageSize, String name, String parent, boolean like) {
    try {
      final HttpGet request = new HttpGet(
          address + "/translator/rest/v1/search?page=" + page + "&pageSize=" + pageSize +
              "&like=" + like +
              (name == null ? "" : "&name=" + URLEncoder.encode(name, "UTF-8")) +
              (parent == null ? "" : "&parent=" + URLEncoder.encode(parent, "UTF-8"))
      );
      request.addHeader("accept", "application/json");
      builder.build();
      try (CloseableHttpClient client = builder.build()) {
        final HttpResponse result = client.execute(request);
        final String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        return gson.fromJson(json, pagedType);
      } catch (Exception e) {
        e.printStackTrace();
      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
