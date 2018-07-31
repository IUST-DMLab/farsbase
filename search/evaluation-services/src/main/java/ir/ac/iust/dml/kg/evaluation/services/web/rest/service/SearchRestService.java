/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.services.web.rest.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.model.SimpleSearchResult;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author morteza.khaleghi
 */
@Service
public class SearchRestService {

    @Value("${evaluation.search.restUrl}")
    private String searcherUrl;

    public String getSearcherUrl() {
        return searcherUrl;
    }

    public void setSearcherUrl(String searcherUrl) {
        this.searcherUrl = searcherUrl;
    }

    public SearchRestService() {
    }

    public List<SimpleSearchResult> search(String q) {
        try {
            List<SimpleSearchResult> simpleSearchResults = new ArrayList<>();
            HttpResponse<JsonNode> searchResponse = Unirest.get(searcherUrl)
                    .queryString("keyword", q)
                    .asJson();
            JSONArray entities = searchResponse.getBody().getObject().getJSONArray("entities");
            for (int i = 0; i < entities.length(); i++) {
                JSONObject entity = entities.getJSONObject(i);
                //evaluting RelationalResults only
                if (entity.has("resultType") && entity.getString("resultType") != null && entity.getString("resultType").equals("RelationalResult")) {
                    String link = "";
                    if (entity.has("link") && !entity.isNull("link")) {
                        link = entity.getString("link");
                    }
                    String title = "";
                    if (entity.has("title") && !entity.isNull("title")) {
                        title = entity.getString("title");
                    }
                    simpleSearchResults.add(new SimpleSearchResult(link, title));
                }
            }
            return simpleSearchResults;
        } catch (Exception ex) {
            return null;
        }
    }
}
