package ir.ac.iust.dml.kg.search.logic.kgservice.data;

public class EntityInfo {
    private String entityUrl;
    private String entityLabel;

    public EntityInfo() {
    }

    public EntityInfo(String entityUrl, String entityLabel) {
        this.entityUrl = entityUrl;
        this.entityLabel = entityLabel;
    }

    public String getEntityUrl() {
        return entityUrl;
    }

    public void setEntityUrl(String entityUrl) {
        this.entityUrl = entityUrl;
    }

    public String getEntityLabel() {
        return entityLabel;
    }

    public void setEntityLabel(String entityLabel) {
        this.entityLabel = entityLabel;
    }
}
