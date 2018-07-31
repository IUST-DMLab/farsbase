package ir.ac.iust.dml.kg.knowledge.store.services.v2.data;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.MapRule;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.PropertyMapping;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Data for define property of a template
 */
@XmlType(name = "PropertyData")
public class PropertyData {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "نام")
    private String property;
    @ApiModelProperty(required = false, example = "2.0")
    private Double weight;
    private List<MapRuleData> rules;
    private List<MapRuleData> recommendations;


    public PropertyMapping fill(PropertyMapping mapping, boolean incremental) {
        if (mapping == null) mapping = new PropertyMapping();
        mapping.setWeight(weight);
        if (rules != null) {
            if(!incremental) mapping.getRules().clear();
            for (MapRuleData r : rules)
                mapping.getRules().add(r.fill(null));
        }
        if (recommendations != null) {
            if(!incremental) mapping.getRecommendations().clear();
            for (MapRuleData r : recommendations)
                mapping.getRecommendations().add(r.fill(null));
        }
        return mapping;
    }

    public PropertyData sync(PropertyMapping mapping) {
        if (mapping == null) return null;
        weight = mapping.getWeight();
        rules = new ArrayList<>();
        for (MapRule r : mapping.getRules())
            rules.add(new MapRuleData().sync(r));
        recommendations = new ArrayList<>();
        for (MapRule r : mapping.getRecommendations())
            recommendations.add(new MapRuleData().sync(r));
        return this;
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
