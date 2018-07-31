package ir.ac.iust.dml.kg.search.logic.kgservice.data;

public class PropertyInfo {
    private String url;
    private String label;
    private Boolean isMandatory;

    public PropertyInfo() {
    }

    public PropertyInfo(String url, String label, Boolean isMandatory) {
        this.url = url;
        this.label = label;
        this.isMandatory = isMandatory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getMandatory() {
        return isMandatory;
    }

    public void setMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }
}
