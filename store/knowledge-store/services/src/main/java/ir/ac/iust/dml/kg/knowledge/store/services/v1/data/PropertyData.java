package ir.ac.iust.dml.kg.knowledge.store.services.v1.data;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.PropertyMapping;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Data for define property of a template
 */
@XmlType(name = "PropertyData")
@Deprecated
public class PropertyData {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "نام")
    private String property;
    @ApiModelProperty(required = false, example = "2.0")
    private Double weight;
    private List<MapRuleData> rules;
    private List<MapRuleData> recommendations;


    public PropertyMapping fill(PropertyMapping mapping) {
        mapping.setWeight(weight);
        if (rules != null)
            for (MapRuleData r : rules)
                mapping.getRules().add(r.fill(null));
        if (recommendations != null)
            for (MapRuleData r : recommendations)
                mapping.getRecommendations().add(r.fill(null));
        return mapping;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<MapRuleData> getRules() {
        return rules;
    }

    public void setRules(List<MapRuleData> rules) {
        this.rules = rules;
    }

    public List<MapRuleData> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<MapRuleData> recommendations) {
        this.recommendations = recommendations;
    }
}
