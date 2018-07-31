package ir.ac.iust.dml.kg.knowledge.store.services.v2.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.MapRule;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Rule data of mapping
 */
@SuppressWarnings("Duplicates")
@XmlType(name = "MapRuleData")
public class MapRuleData {
    @NotNull
    @URL
    @ApiModelProperty(required = true, example = "http://www.w3.org/TR/rdf-schema/type")
    private String predicate;
    @ApiModelProperty(example = "http://knowledgegraph.ir/mananger")
    private String constant;
    @ApiModelProperty(example = "Resource")
    private ValueType type;
    @ApiModelProperty(example = "Reserved")
    private String unit;
    @ApiModelProperty(example = "Reserved")
    private String transform;

    public MapRule fill(MapRule o) {
        if (o == null)
            o = new MapRule();
        o.setPredicate(predicate);
        o.setConstant(constant);
        o.setType(type);
        o.setUnit(unit);
        o.setTransform(transform);
        return o;
    }

    MapRuleData sync(MapRule o) {
        if (o == null) return null;
        predicate = o.getPredicate();
        constant = o.getConstant();
        type = o.getType();
        unit = o.getUnit();
        transform = o.getTransform();
        return this;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }
}
