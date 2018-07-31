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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Data class for subject
 * Each subject has many triples
 * Each triples stored in triples map {predicate: Triple}
 * Each predicate can have multi value
 * * Sometimes value are from multi source
 * * Sometimes value are multi in value for example jobs of a person
 * * Sometimes value are diff in url of source
 */
@XmlType(name = "Subject", namespace = "http://kg.dml.iust.ac.ir")
@Document(collection = "subjects")
@CompoundIndexes({
        @CompoundIndex(name = "subject_index", def = "{'context': 1, 'subject' : 2}", unique = true)
})
public class Subject {
    @Id
    @JsonIgnore
    private ObjectId id;
    private String context;
    private String subject;
    private long creationEpoch;
    private long modificationEpoch;
    private HashMap<String, ArrayList<TripleObject>> triples;
    /**
     * Set of source that has triple for voting
     */
    @Indexed
    private Set<String> sourcesNeedVote;
    /**
     * Indexed list of voters to fast search subject that voter can vote in format identifier@moduel
     * Voter must voted all need vote
     */
    @Indexed
    private Set<String> voters;

    public Subject() {
    }

    public Subject(String context, String subject) {
        this.context = context;
        this.subject = subject;
        this.creationEpoch = System.currentTimeMillis();
    }

    public String getIdentiier() {
        return id != null ? id.toString() : null;
    }

    public TripleObject addObject(String predicate, TypedValue value, String module, String url) {
        if (triples == null) triples = new HashMap<>();
        final ArrayList<TripleObject> objects;
        if (triples.containsKey(predicate))
            objects = triples.get(predicate);
        else {
            objects = new ArrayList<>();
            triples.put(predicate, objects);
        }
        final TripleObject o2 = new TripleObject(value, module, url);
        for (TripleObject o : objects)
            if (o.equals(o2))
                return o;
        objects.add(o2);
        return o2;
    }

    public void updateSourcesNeedVote() {
        sourcesNeedVote = new HashSet<>();
        triples.forEach((k, v) ->
                v.forEach(t -> {
                    if (t.getState() != TripleState.Approved)
                        sourcesNeedVote.add(t.getSource().getModule());
                })
        );
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

    public HashMap<String, ArrayList<TripleObject>> getTriples() {
        if (triples == null) triples = new HashMap<>();
        return triples;
    }

    public void setTriples(HashMap<String, ArrayList<TripleObject>> triples) {
        this.triples = triples;
    }

    public Set<String> getSourcesNeedVote() {
        if (sourcesNeedVote == null) sourcesNeedVote = new HashSet<>();
        return sourcesNeedVote;
    }

    public void setSourcesNeedVote(Set<String> sourcesNeedVote) {
        this.sourcesNeedVote = sourcesNeedVote;
    }

    public Set<String> getVoters() {
        if (voters == null) voters = new HashSet<>();
        return voters;
    }

    public void setVoters(Set<String> voters) {
        this.voters = voters;
    }
}
