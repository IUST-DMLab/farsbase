package ir.ac.iust.dml.kg.resource.extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Cache all resource
 */
public class AllResourceCache {
    private Map<String, Resource> allResource = new HashMap<>();

    public Resource addOrUpdate(Resource r, ILabelConverter converter, Set<String> newLabels) {
        final Resource old = allResource.get(r.getIri());
        r.getVariantLabel().forEach(l -> {
            if (converter != null)
                converter.convert(l).forEach(l2 -> {
                    if (old == null || !old.getVariantLabel().contains(l2))
                        newLabels.add(l2);
                });
            else if (old == null || !old.getVariantLabel().contains(l))
                newLabels.add(l);
        });
        final Resource current;
        if (old == null) {
            current = r;
            allResource.put(current.getIri(), current);
        } else {
            current = old;
            if (r.getLabel() != null)
                old.setLabel(r.getLabel());
            if (r.getInstanceOf() != null)
                old.setInstanceOf(r.getInstanceOf());
            if (r.getType() != null)
                old.setType(r.getType());
            if (!r.getClassTree().isEmpty())
                old.getClassTree().addAll(r.getClassTree());
            old.getVariantLabel().addAll(newLabels);
        }
        return current;
    }

    public Resource get(String iri) {
        return allResource.get(iri);
    }
}
