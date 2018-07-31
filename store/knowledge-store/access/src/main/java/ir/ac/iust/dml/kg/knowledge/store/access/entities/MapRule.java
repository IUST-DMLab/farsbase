package ir.ac.iust.dml.kg.knowledge.store.access.entities;

import ir.ac.iust.dml.kg.knowledge.core.ValueType;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Mapped result
 */
@Deprecated
public class MapRule {
    private String predicate;
    private String constant;
    private ValueType type;
    private String unit;
    private String transform;

    public MapRule() {
    }

    public MapRule(String predicate, String constant, ValueType type) {
        this.predicate = predicate;
        this.constant = constant;
        this.type = type;
    }

    public MapRule(String predicate, ValueType type) {
        this.predicate = predicate;
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapRule mapRule = (MapRule) o;

        if (predicate != null ? !predicate.equals(mapRule.predicate) : mapRule.predicate != null) return false;
        if (constant != null ? !constant.equals(mapRule.constant) : mapRule.constant != null) return false;
        return transform != null ? transform.equals(mapRule.transform) : mapRule.transform == null;
    }

    @Override
    public int hashCode() {
        int result = predicate != null ? predicate.hashCode() : 0;
        result = 31 * result + (constant != null ? constant.hashCode() : 0);
        result = 31 * result + (transform != null ? transform.hashCode() : 0);
        return result;
    }
}
