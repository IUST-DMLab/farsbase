/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.services.web.rest.model;

import ir.ac.iust.dml.kg.evaluation.model.Query;
import java.util.List;

/**
 *
 * @author morteza.khaleghi
 */
public class QueryResult {
    private Query query;
    private List<SimpleSearchResult> searchResults;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public List<SimpleSearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SimpleSearchResult> searchResults) {
        this.searchResults = searchResults;
    }
    
    
}
