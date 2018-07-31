package ir.ac.iust.dml.kg.knowledge.core;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * TypedValue of object
 * A value can have type and language
 * Language must be fa or en
 * Value always stored in string format
 */
public class TypedValue {
    private ValueType type;
    private String value;
    private String lang;

    public TypedValue() {
    }

    public TypedValue(ValueType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TypedValue(ValueType type, String value, String lang) {
        this.type = type;
        this.value = value;
        this.lang = lang;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        if (lang != null)
            return String.format("\"%s\"^^%s@%s}", value, type, lang);
        else
            return String.format("\"%s\"^^%s}", value, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedValue that = (TypedValue) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
