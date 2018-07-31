package ir.ac.iust.dml.kg.knowledge.store.services.v2.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.MapRule;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.PropertyMapping;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TemplateMapping;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Data for define template
 */
@SuppressWarnings("Duplicates")
@XmlType(name = "TemplateData")
public class TemplateData {
    private String identifier;
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "شخص")
    private String template;
    @Valid
    private List<PropertyData> properties;
    private List<MapRuleData> rules;
    private Double weight;
    private boolean incremental = true;

    public TemplateMapping fill(TemplateMapping mapping) {
        if (mapping == null)
            mapping = new TemplateMapping(template);
        else
            assert template.equals(mapping.getTemplate());
        if (rules != null) {
            if(!incremental) mapping.getRules().clear();
            for (MapRuleData r : rules)
                mapping.getRules().add(r.fill(null));
        }
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
                property.fill(old, incremental);
            }
        mapping.setWeight(weight);
        return mapping;
    }

    public TemplateData sync(TemplateMapping mapping) {
        if (mapping == null) return null;
        identifier = mapping.getIdentifier();
        template = mapping.getTemplate();
        rules = new ArrayList<>();
        for (MapRule r : mapping.getRules())
            rules.add(new MapRuleData().sync(r));
        properties = new ArrayList<>();
        for (PropertyMapping p : mapping.getProperties())
            properties.add(new PropertyData().sync(p));
        weight = mapping.getWeight();
        return this;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }
}
