package ir.ac.iust.dml.kg.knowledge.store.services.v1.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.MapRule;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;

/**
 * Rule data of mapping
 */
@XmlType(name = "MapRuleData")
@Deprecated
public class MapRuleData {
    @NotNull
    @URL
    @ApiModelProperty(required = true, example = "http://www.w3.org/TR/rdf-schema/type")
    private String predicate;
    @ApiModelProperty(required = false, example = "http://knowledgegraph.ir/mananger")
    private String constant;
    @ApiModelProperty(required = false, example = "Resource")
    private ValueType type;
    @ApiModelProperty(required = false, example = "Reserved")
    private String unit;
    @ApiModelProperty(required = false, example = "Reserved")
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
