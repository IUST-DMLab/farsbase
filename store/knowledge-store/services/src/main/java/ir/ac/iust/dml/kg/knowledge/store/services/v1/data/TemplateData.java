package ir.ac.iust.dml.kg.knowledge.store.services.v1.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.PropertyMapping;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TemplateMapping;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Data for define template
 */
@XmlType(name = "TemplateData")
@Deprecated
public class TemplateData {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "شخص")
    private String template;
    @Valid
    private List<PropertyData> properties;
    private List<MapRuleData> rules;
    private Double weight;

    public TemplateMapping fill(TemplateMapping mapping) {
        if (mapping == null)
            mapping = new TemplateMapping(template);
        else
            assert template.equals(mapping.getTemplate());
        if (rules != null)
            for (MapRuleData r : rules)
                mapping.getRules().add(r.fill(null));
        if (properties != null)
            for (PropertyData property : properties) {
                PropertyMapping old = null;
                for (PropertyMapping m : mapping.getProperties())
                    if (m.getProperty().equals(property.getProperty()))
                        old = m;
                if (old == null) {
                    old = new PropertyMapping(template, property.getProperty());
                    mapping.getProperties().add(old);
                }
                property.fill(old);
            }
        mapping.setWeight(weight);
        return mapping;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<PropertyData> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyData> properties) {
        this.properties = properties;
    }

    public List<MapRuleData> getRules() {
        return rules;
    }

    public void setRules(List<MapRuleData> rules) {
        this.rules = rules;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
