package ir.ac.iust.dml.kg.search.logic.kgservice.data;

public class PropertyValue {
    private String content;
    private String url;

    public PropertyValue() {
    }

    public PropertyValue(String content, String url) {
        this.content = content;
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
