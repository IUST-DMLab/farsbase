package ir.ac.iust.dml.kg.search.logic.kgservice.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropertyData {
    private String propUrl;
    private String propLabel;
    private List<PropertyValue> propValue = new ArrayList<>();

    public PropertyData() {
    }

    public PropertyData(String propUrl, String propLabel) {
        this.propUrl = propUrl;
        this.propLabel = propLabel;
    }

    public PropertyData(String propUrl, String propLabel, PropertyValue ... firstValue) {
        this.propUrl = propUrl;
        this.propLabel = propLabel;
        Collections.addAll(propValue, firstValue);
    }

    public String getPropUrl() {
        return propUrl;
    }

    public void setPropUrl(String propUrl) {
        this.propUrl = propUrl;
    }

    public String getPropLabel() {
        return propLabel;
    }

    public void setPropLabel(String propLabel) {
        this.propLabel = propLabel;
    }

    public List<PropertyValue> getPropValue() {
        return propValue;
    }

    public void setPropValue(List<PropertyValue> propValue) {
        this.propValue = propValue;
    }
}
