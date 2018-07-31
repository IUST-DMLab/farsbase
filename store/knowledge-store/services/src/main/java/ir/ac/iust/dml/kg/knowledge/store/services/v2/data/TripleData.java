package ir.ac.iust.dml.kg.knowledge.store.services.v2.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Subject;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleObject;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.validation.ValidTypedValue;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@SuppressWarnings("WeakerAccess")
@XmlType(name = "TripleData")
public class TripleData {
    @URL
    @ApiModelProperty(required = false, example = "http://kg.dml.iust.ac.ir")
    private String context;
    @NotNull
    @NotEmpty
    @URL
    @ApiModelProperty(required = true, example = "http://knowledgegraph.ir/Esteghlal_F.C.")
    private String subject;
    @NotNull
    @NotEmpty
    @URL
    @ApiModelProperty(required = true, example = "http://knowledgegraph.ir/mananger")
    private String predicate;
    @NotNull
    @Valid
    @ValidTypedValue
    @ApiModelProperty(required = true)
    private TypedValueData object;
    @Valid
    private HashMap<String, TypedValueData> properties;
    @NotNull
    @NotEmpty
    @ApiModelProperty(value = "Module that triples was extracted from it", required = true, example = "wikipedia/infobox")
    private String module;
    @ApiModelProperty(value = "Version of triple, if be null replace it bu nextVersion of module", required = false, example = "1")
    private Integer version;
    @NotNull
    @NotEmpty
    @URL
    @ApiModelProperty(value = "Page url that triples was extracted from it", required = true, example = "https://en.wikipedia.org/wiki/Esteghlal_F.C.")
    private String url;
    @ApiModelProperty(value = "Additional parameter that module used to extract data", required = false, example = "{version: 2}")
    private HashMap<String, String> parameters;
    private Double precession;
    @ApiModelProperty(value = "If true (state = approved) else if false (state = reject) else if is null (nothing)", required = false, example = "{version: 2}")
    private Boolean approved;

    public Subject fill(Subject s) {
        if (s == null)
            s = new Subject(context, subject);
        else
            assert s.getSubject().equals(subject) && s.getContext().equals(context);
        final TripleObject obj = s.addObject(predicate, object.fill(null), module, url);
        final HashMap<String, TypedValue> properties = new HashMap<>();
        if (this.properties != null)
            this.properties.forEach((k, v) -> properties.put(k, v.fill(null)));
        if (approved)
            obj.setState(TripleState.Approved);
        else if (obj.getState() == TripleState.Rejected && !properties.equals(obj.getProperties()))
            obj.setState(TripleState.None);
        else if (obj.getState() == TripleState.Approved && !properties.equals(obj.getProperties()))
            obj.setState(TripleState.None);
        obj.setProperties(properties);
        obj.getSource().setVersion(version);
        obj.getSource().setParameters(parameters);
        obj.getSource().setPrecession(precession);

        obj.setModificationEpoch(System.currentTimeMillis());
        return s;
    }

    public TripleData sync(String context, String subject, String predicate, TripleObject tripleObject) {
        this.context = context;
        this.subject = subject;
        this.predicate = predicate;
        this.properties = new HashMap<>();
        this.object = new TypedValueData().sync(tripleObject);
        tripleObject.getProperties().forEach((k, v) -> this.properties.put(k, new TypedValueData().sync(v)));
        if (tripleObject.getSource() != null) {
            this.module = tripleObject.getSource().getModule();
            this.parameters = new HashMap<>();
            this.parameters.putAll(tripleObject.getSource().getParameters());
            this.precession = tripleObject.getSource().getPrecession();
            this.url = tripleObject.getSource().getUrl();
            this.version = tripleObject.getSource().getVersion();
        }
        return this;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public TypedValueData getObject() {
        return object;
    }

    public void setObject(TypedValueData object) {
        this.object = object;
    }

    public HashMap<String, TypedValueData> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, TypedValueData> properties) {
        this.properties = properties;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public Double getPrecession() {
        return precession;
    }

    public void setPrecession(Double precession) {
        this.precession = precession;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
