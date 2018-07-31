package ir.ac.iust.dml.kg.knowledge.store.services.v2.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Ontology;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.validation.ValidTypedValue;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
public class OntologyData {
    private String identifier;
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
    @ApiModelProperty(value = "If true (state = approved) else if false (state = reject) else if is null (nothing)", required = false, example = "{version: 2}")
    private Boolean approved;

    public Ontology fill(Ontology ontology) {
        if (ontology != null) {
            assert subject.equals(ontology.getSubject()) && object.getValue().equals(ontology.getObject().getValue())
                    && predicate.equals(ontology.getPredicate());
            object.fill(ontology.getObject());
        } else
            ontology = new Ontology(context, subject, predicate, object.fill(null));
        ontology.setModificationEpoch(System.currentTimeMillis());
        if (approved != null && approved)
            ontology.setState(TripleState.Approved);
        else if (approved != null)
            ontology.setState(TripleState.Rejected);
        return ontology;
    }

    public OntologyData sync(Ontology ontology) {
        if(ontology == null) return null;
        identifier = ontology.getIdentifier();
        context = ontology.getContext();
        subject = ontology.getSubject();
        predicate = ontology.getPredicate();
        object = new TypedValueData().sync(ontology.getObject());
        approved = ontology.getState() == TripleState.Approved;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
