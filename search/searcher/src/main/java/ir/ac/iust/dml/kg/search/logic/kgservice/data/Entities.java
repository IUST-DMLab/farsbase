package ir.ac.iust.dml.kg.search.logic.kgservice.data;

import java.util.ArrayList;
import java.util.List;

public class Entities {
    private List<EntityInfo> result = new ArrayList<>();

    public List<EntityInfo> getResult() {
        return result;
    }

    public void setResult(List<EntityInfo> result) {
        this.result = result;
    }
}
