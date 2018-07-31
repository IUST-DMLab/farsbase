package ir.ac.iust.dml.kg.search.logic.data;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private List<String> breadcrumb = new ArrayList<String>();
    private List<ResultEntity> entities = new ArrayList<ResultEntity>();

    public List<String> getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(List<String> breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public List<ResultEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<ResultEntity> entities) {
        this.entities = entities;
    }
}
