package ir.ac.iust.dml.kg.resource.extractor;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Resource that match
 */
public class MatchedResource {
    private final int start;
    private final int end;
    private final Resource resource;
    private final Set<Resource> ambiguities;
    private MatchedResource subsetOf;

    public MatchedResource(int start, int end, Resource resource, Set<Resource> ambiguities) {
        this.start = start;
        this.end = end;
        this.resource = resource == null ? null : new Resource(resource);
        this.ambiguities = new HashSet<>();
        if (ambiguities != null)
            ambiguities.forEach(a -> this.ambiguities.add(new Resource(a)));
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Resource getResource() {
        return resource;
    }

    public Set<Resource> getAmbiguities() {
        return ambiguities;
    }

    public MatchedResource getSubsetOf() {
        return subsetOf;
    }

    public void setSubsetOf(MatchedResource subsetOf) {
        this.subsetOf = subsetOf;
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        if (ambiguities != null && !ambiguities.isEmpty())
            ambiguities.forEach(a -> str.append(String.format("or(%s)", a)));
        if (subsetOf != null && ambiguities != null && !ambiguities.isEmpty())
            return String.format("[%d %d] %s < (%s)", start, end, str, subsetOf);
        if (subsetOf != null)
            return String.format("[%d %d] %s < (%s)", start, end, resource, subsetOf);
        if (ambiguities != null && !ambiguities.isEmpty())
            return String.format("[%d %d] %s", start, end, str);
        return String.format("[%d %d] %s", start, end, resource);

    }
}