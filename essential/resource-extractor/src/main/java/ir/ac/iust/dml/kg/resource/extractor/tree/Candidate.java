package ir.ac.iust.dml.kg.resource.extractor.tree;

import ir.ac.iust.dml.kg.resource.extractor.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.Resource;
import ir.ac.iust.dml.kg.resource.extractor.ResourceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Candidate of entity must not entity yet
 */
public class Candidate {
    private final int start;
    private boolean valid;
    private final List<TreeNode> path = new ArrayList<>();


    Candidate(int start, TreeNode node) {
        this.start = start;
        this.valid = true;
        this.path.add(node);
    }

    Candidate extend(String word) {
        if (!valid) return this;
        final TreeNode node = lastNode().extend(word);
        if (node != null)
            path.add(node);
        else
            valid = false;
        return this;
    }

    private TreeNode lastNode() {
        return path.get(path.size() - 1);
    }

    List<MatchedResource> createResource(boolean removeSubset, boolean removeCategory) {
        final List<MatchedResource> result = new ArrayList<>();
        for (int j = path.size() - 1; j >= 0; j--) {
            Resource resource = path.get(j).getResource();
            Set<Resource> ambiguities = path.get(j).getAmbiguities();
            if(removeCategory) {
                if(resource != null && resource.getType() != null && resource.getType() == ResourceType.Category)
                    resource = null;
                ambiguities = ambiguities.stream()
                        .filter(a -> a.getType() != ResourceType.Category).collect(Collectors.toSet());
                if(resource == null && ambiguities.size() == 1) {
                    resource = ambiguities.iterator().next();
                    ambiguities.clear();
                }
            }
            if (resource != null || !ambiguities.isEmpty()) {
                final MatchedResource m = new MatchedResource(start, start + j - 1, resource, ambiguities);
                result.add(m);
                if (removeSubset)
                    break;
            }
        }
        return result;
    }
}
