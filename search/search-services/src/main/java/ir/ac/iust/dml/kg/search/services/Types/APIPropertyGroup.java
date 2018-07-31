package ir.ac.iust.dml.kg.search.services.Types;

import java.util.List;

/**
 * Created by ali on 6/22/17.
 */
public class APIPropertyGroup {
    private int order;
    private List<APIPropertySingle> result;
    private double confidence;

    public APIPropertyGroup(int order, List<APIPropertySingle> result, double confidence) {
        this.order = order;
        this.result = result;
        this.confidence = confidence;
    }

    public int getOrder() {
        return order;
    }

    public List<APIPropertySingle> getResult() {
        return result;
    }

    public double getConfidence() {
        return confidence;
    }
}
