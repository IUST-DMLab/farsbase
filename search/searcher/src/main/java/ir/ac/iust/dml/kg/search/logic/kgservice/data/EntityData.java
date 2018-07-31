package ir.ac.iust.dml.kg.search.logic.kgservice.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityData {
    private String label;
    private Map<String, PropertyData> propertyMap = new HashMap<>();

    public EntityData() {
    }

    public EntityData(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<PropertyData> getProperties() {
        return propertyMap.values();
    }

    @JsonIgnore
    public Map<String, PropertyData> getPropertyMap() {
        return propertyMap;
    }

}
