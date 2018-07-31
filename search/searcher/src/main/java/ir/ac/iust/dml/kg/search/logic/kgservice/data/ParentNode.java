package ir.ac.iust.dml.kg.search.logic.kgservice.data;

public class ParentNode {
    private String parentUrl;
    private String label;

    public ParentNode() {
    }

    public ParentNode(String parentUrl, String label) {
        this.parentUrl = parentUrl;
        this.label = label;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}