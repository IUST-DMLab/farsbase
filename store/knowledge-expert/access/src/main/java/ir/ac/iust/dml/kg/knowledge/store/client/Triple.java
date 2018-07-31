package ir.ac.iust.dml.kg.knowledge.store.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * data class for triples
 * http://194.225.227.161:8081/browse/KG-180
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Triple {
    private String identifier;
    private String context;
    private String subject;
    private String predicate;
    private TypedValue object;
    private Set<Source> sources;

    public Triple() {
    }

    public Triple(String identifier) {
        this.identifier = identifier;
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

    public TypedValue getObject() {
        return object;
    }

    public void setObject(TypedValue object) {
        this.object = object;
    }

    public Set<Source> getSources() {
        return sources;
    }

    public void setSources(Set<Source> sources) {
        this.sources = sources;
    }
}

