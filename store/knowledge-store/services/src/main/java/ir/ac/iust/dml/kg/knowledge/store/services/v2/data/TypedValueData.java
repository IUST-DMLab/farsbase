package ir.ac.iust.dml.kg.knowledge.store.services.v2.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Data for typed value
 */
@SuppressWarnings("Duplicates")
@XmlType(name = "TypedValueData")
public class TypedValueData {
    @NotNull
    @ApiModelProperty(required = true, example = "Resource")
    private ValueType type;
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "http://knowledgegraph.ir/Alireza_Mansourian")
    private String value;
    @ApiModelProperty(required = false, example = "fa")
    private String lang;

    TypedValue fill(TypedValue object) {
        if (object == null)
            object = new TypedValue();
        object.setType(type);
        object.setValue(value);
        object.setLang(lang);
        return object;
    }

    TypedValueData sync(TypedValue obj) {
        if (obj == null) return null;
        type = obj.getType();
        value = obj.getValue();
        lang = obj.getLang();
        return this;
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
            return String.format("\"%s\"^^%s@%s}", value, type, lang);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        return result;
    }
}
