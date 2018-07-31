package ir.ac.iust.dml.kg.search.services.Types;

/**
 * Created by ali on 6/22/17.
 */
public class APIEntity {
    private int order;
    private String content;
    private String url;
    private double confidence;

    public APIEntity(int order, String content, String url, double confidence) {
        this.order = order;
        this.content = content;
        this.url = url;
        this.confidence = confidence;
    }

    public int getOrder() {
        return order;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public double getConfidence() {
        return confidence;
    }
}
