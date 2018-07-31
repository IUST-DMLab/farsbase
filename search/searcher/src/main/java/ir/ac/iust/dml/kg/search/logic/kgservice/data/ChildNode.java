package ir.ac.iust.dml.kg.search.logic.kgservice.data;

public class ChildNode {
    private String nodeUrl;
    private String label;

    public ChildNode() {
    }

    public ChildNode(String nodeUrl, String label) {
        this.nodeUrl = nodeUrl;
        this.label = label;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
