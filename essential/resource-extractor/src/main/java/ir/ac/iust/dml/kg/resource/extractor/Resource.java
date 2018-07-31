package ir.ac.iust.dml.kg.resource.extractor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Resource Data class
 */
public class Resource implements Serializable {
    private String iri;
    private ResourceType type;
    private String instanceOf;
    private final Set<String> classTree = new HashSet<>();
    private String label;
    private final Set<String> variantLabel = new HashSet<>();

    /**
     * Copy constructor
     */
    public Resource(Resource copy) {
        this.iri = copy.iri;
        this.type = copy.type;
        this.instanceOf = copy.instanceOf;
        this.classTree.addAll(copy.classTree);
        this.label = copy.label;
        this.variantLabel.addAll(copy.variantLabel);
    }

    public Resource(String iri) {
        this.iri = iri;
    }

    public Resource(String iri, String label, String... variantLabel) {
        this.iri = iri;
        this.label = label;
        this.variantLabel.add(label);
        Collections.addAll(this.variantLabel, variantLabel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        return iri != null ? iri.equals(resource.iri) : resource.iri == null;
    }

    @Override
    public int hashCode() {
        return iri != null ? iri.hashCode() : 0;
    }

    public boolean hasData() {
        return type != null || instanceOf != null || !classTree.isEmpty() ||
                label != null || !variantLabel.isEmpty();
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getInstanceOf() {
        return instanceOf;
    }

    public void setInstanceOf(String instanceOf) {
        this.instanceOf = instanceOf;
    }

    public Set<String> getClassTree() {
        return classTree;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<String> getVariantLabel() {
        return variantLabel;
    }

    @Override
    public String toString() {
        return String.format("<%s>", iri);
    }
}
