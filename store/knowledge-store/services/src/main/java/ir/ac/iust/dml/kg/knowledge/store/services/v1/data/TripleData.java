package ir.ac.iust.dml.kg.knowledge.store.services.v1.data;


import cz.jirutka.validator.collection.constraints.EachURL;
import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Source;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Triple;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.services.v1.validation.ValidTypedValue;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@XmlType(name = "TripleData")
@Deprecated
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
    @NotNull
    @NotEmpty
    @ApiModelProperty(value = "Module that triples was extracted from it", required = true, example = "wikipedia/infobox")
    private String module;
    @ApiModelProperty(value = "Version of triple, if be null replace it bu nextVersion of module", required = false, example = "1")
    private Integer version;
    @NotNull
    @NotEmpty
    @EachURL
    @ApiModelProperty(value = "Page url that triples was extracted from it", required = true, example = "https://en.wikipedia.org/wiki/Esteghlal_F.C.")
    private List<String> urls;
    @ApiModelProperty(value = "Additional parameter that module used to extract data", required = false, example = "{version: 2}")
    private HashMap<String, String> parameters;
    private Double precession;
    @ApiModelProperty(value = "If true (state = approved) else if false (state = reject) else if is null (nothing)", required = false, example = "{version: 2}")
    private Boolean approved;

    public Triple fill(Triple triple) {
        if (triple != null) {
            assert subject.equals(triple.getSubject()) && object.getValue().equals(triple.getObject().getValue())
                    && predicate.equals(triple.getPredicate());
            object.fill(triple.getObject());
        } else
            triple = new Triple(context, subject, predicate, object.fill(null));
        boolean found = false;
        for (Source s : triple.getSources())
            if (s.getModule().equals(module)) {
                s.getUrls().addAll(urls);
                s.setPrecession(precession);
                if (parameters != null)
                    s.getParameters().putAll(parameters);
                s.setVersion(version);
                found = true;
            }

        if (!found) {
            triple.getSources().add(new Source(module, version, urls, parameters, precession));
        }
        triple.setModificationEpoch(System.currentTimeMillis());
        if (approved != null && approved)
            triple.setState(TripleState.Approved);
        else if (approved != null)
            triple.setState(TripleState.Rejected);
        return triple;
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

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
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
