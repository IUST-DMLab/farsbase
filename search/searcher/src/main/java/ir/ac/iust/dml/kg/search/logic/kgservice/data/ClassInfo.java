package ir.ac.iust.dml.kg.search.logic.kgservice.data;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    private String label;
    private List<PropertyInfo> properties = new ArrayList<>();

    public ClassInfo() {
    }

    public ClassInfo(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<PropertyInfo> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyInfo> properties) {
        this.properties = properties;
    }
}
