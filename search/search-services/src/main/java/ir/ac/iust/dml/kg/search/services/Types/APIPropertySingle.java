package ir.ac.iust.dml.kg.search.services.Types;

/**
 * Created by ali on 6/22/17.
 */
public class APIPropertySingle {
    private String content;
    private String type;
    private String referenceUrl;
    private String url;

    public APIPropertySingle(String content, String type, String referenceUrl, String url) {
        this.content = content;
        this.type = type;
        this.referenceUrl = referenceUrl;
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public String getUrl() {
        return url;
    }
}
