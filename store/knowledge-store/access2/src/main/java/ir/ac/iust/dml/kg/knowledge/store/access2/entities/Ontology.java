package ir.ac.iust.dml.kg.knowledge.store.access2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlType;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Entity to keep ontology it differ from triple because it has not source and not updated by extractors
 */
@XmlType(name = "Ontology", namespace = "http://kg.dml.iust.ac.ir")
@Document(collection = "ontology")
@CompoundIndexes({
        @CompoundIndex(name = "triple_index", def = "{'context': 1, 'subject' : 2, 'predicate' : 3, 'object.value': 4}", unique = true),
        @CompoundIndex(name = "subject_predicate_index", def = "{'subject' : 1, 'predicate' : 2}"),
        @CompoundIndex(name = "predicate_object_index", def = "{'predicate' : 1, 'object.value' : 2}}")
})
public class Ontology {
    @Id
    @JsonIgnore
    private ObjectId id;
    private String context;
    @Indexed
    private String subject;
    @Indexed
    private String predicate;
    private TypedValue object;
    private long creationEpoch;
    private long modificationEpoch;
    private TripleState state;

    public Ontology() {
    }

    public Ontology(String context, String subject, String predicate, TypedValue object) {
        this.context = context;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.creationEpoch = this.modificationEpoch = System.currentTimeMillis();
        this.state = TripleState.None;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public TypedValue getObject() {
        return object;
    }

    public void setObject(TypedValue object) {
        this.object = object;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public long getModificationEpoch() {
        return modificationEpoch;
    }

    public void setModificationEpoch(long modificationEpoch) {
        this.modificationEpoch = modificationEpoch;
    }

    public TripleState getState() {
        return state;
    }

    public void setState(TripleState state) {
        this.state = state;
    }

    public String getIdentifier() {
        return id != null ? id.toString() : null;
    }

    @Override
    public String toString() {
        return String.format("Ontology{%s <%s %s %s>", context, subject, predicate, object);
    }
}
