/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.resource.extractor.client;

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
import java.util.List;

public class ExtractorClient {
  private final String address;
  private final HttpClientBuilder builder = HttpClientBuilder.create();
  private final Type listType = new TypeToken<List<MatchedResource>>() {
  }.getType();
  private final Gson gson = new Gson();


  public ExtractorClient(String address) {
    this.address = address;
    builder.setDefaultRequestConfig(RequestConfig.DEFAULT);
  }

  public List<MatchedResource> match(String text) {
    return match(text, false);
  }

  public List<MatchedResource> match(String text, boolean removeSubset) {
    try {
      final HttpGet request = new HttpGet(
          address + "/rest/v1/extractor/extract?text="
              + URLEncoder.encode(text, "UTF-8") + "&removeSubset=" + removeSubset);
      request.addHeader("accept", "application/json");
      builder.build();
      try (CloseableHttpClient client = builder.build()) {
        final HttpResponse result = client.execute(request);
        final String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        return gson.fromJson(json, listType);
      } catch (Exception e) {
        e.printStackTrace();
      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
