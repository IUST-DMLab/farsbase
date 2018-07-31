package ir.ac.iust.dml.kg.resource.extractor.tree;

import ir.ac.iust.dml.kg.resource.extractor.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Create a tree on labels of resource for fast matching
 */
public class TreeResourceExtractor implements IResourceExtractor {
    private TreeNode root = new TreeNode();
    static final Logger LOGGER = LogManager.getLogger(TreeResourceExtractor.class);
    private final AllResourceCache cache = new AllResourceCache();
    @Override
    public void setup(IResourceReader reader, int pageSize) throws Exception {
        setup(reader, null, pageSize);
    }

    @Override
    public void setup(IResourceReader reader, ILabelConverter converter, int pageSize) throws Exception {
        LOGGER.info("Start create index");
        //noinspection Duplicates
        while (!reader.isFinished()) {
            final List<Resource> resources = reader.read(pageSize);
            resources.forEach(r -> {
                final Set<String> newLabels = new HashSet<>();
                final Resource current = cache.addOrUpdate(r, converter, newLabels);
                newLabels.forEach(l -> root.add(current, l.split("\\s", -1), 0));
            });
        }
        LOGGER.info("Succeed to create index");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public List<MatchedResource> search(String text, Boolean removeSubset, Boolean removeCategory) {
        if (root == null) return null;
        final String[] words = text.split("\\s", -1);
        final List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            final String word = words[i];
            candidates.add(new Candidate(i, root));
            candidates.forEach(c -> c.extend(word));
        }
        final List<MatchedResource> resources = new ArrayList<>();
        MatchedResource currentBestMatch = null;
        for (Candidate c : candidates) {
            final List<MatchedResource> newResources = c.createResource(removeSubset, removeCategory);
            for (MatchedResource n : newResources) {
                if (removeCategory != null && removeCategory && n.getResource() != null &&
                        n.getResource().getType() == ResourceType.Category) continue;
                if (currentBestMatch != null && n.getEnd() <= currentBestMatch.getEnd()) {
                    if (!removeSubset) {
                        n.setSubsetOf(currentBestMatch);
                        resources.add(n);
                    }
                } else {
                    resources.add(n);
                    currentBestMatch = n;
                }
            }
        }
        return resources;
    }

    @Override
    public Resource getResourceByIRI(String iri) {
        return cache.get(iri);
    }
}
