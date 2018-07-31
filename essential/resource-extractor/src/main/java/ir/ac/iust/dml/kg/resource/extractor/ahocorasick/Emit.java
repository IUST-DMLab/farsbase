package ir.ac.iust.dml.kg.resource.extractor.ahocorasick;

import ir.ac.iust.dml.kg.resource.extractor.Resource;

import java.util.HashSet;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Emit of a state
 */
class Emit {
    private final String[] words;
    private Resource resource = null;
    private final Set<Resource> ambiguities = new HashSet<>();

    Emit(String[] words) {
        this.words = words;
    }

    public void add(Resource resource) {
        if (this.resource == null && ambiguities.isEmpty())
            this.resource = resource;
        else {//Words is repeated so it is ambiguity
            if (this.resource != null) {
                this.ambiguities.add(this.resource);
                this.resource = null;
            }
            this.ambiguities.add(resource);
        }
    }

    public String[] getWords() {
        return words;
    }

    public Resource getResource() {
        return resource;
    }

    public Set<Resource> getAmbiguities() {
        return ambiguities;
    }

    @Override
    public String toString() {
        if (resource != null) return resource.toString();
        final StringBuilder sb = new StringBuilder().append("{");
        ambiguities.forEach(a -> sb.append(a).append(", "));
        sb.append("}");
        return sb.toString();
    }
}
