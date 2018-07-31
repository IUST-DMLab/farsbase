package ir.ac.iust.dml.kg.resource.extractor.tree;

import ir.ac.iust.dml.kg.resource.extractor.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * A node of tree
 */
class TreeNode {
    private String path;
    private Resource resource;
    private final Set<Resource> ambiguities = new HashSet<>();
    private final Map<String, TreeNode> childs = new HashMap<>();

    public TreeNode() {
        path = "/";
    }

    TreeNode(String[] path, int length) {
        final StringBuilder p = new StringBuilder();
        for (int i = 0; i <= length; i++)
            p.append(path[i]).append(' ');
        this.path = p.toString();
    }

    void add(Resource resource, String[] path, int position) {
        if (position == path.length) {
            if (!ambiguities.isEmpty())
                ambiguities.add(resource);
            else if (this.resource == null || this.resource.equals(resource))
                this.resource = resource;
            else if (this.resource != null) {
                ambiguities.add(resource);
                ambiguities.add(this.resource);
                this.resource = null;
            } else
                ambiguities.add(resource);
        } else {
            final String urn = path[position];
            TreeNode current = childs.computeIfAbsent(urn, k -> new TreeNode(path, position));
            current.add(resource, path, position + 1);
        }
    }

    public Resource getResource() {
        return resource;
    }

    public Set<Resource> getAmbiguities() {
        return ambiguities;
    }

    TreeNode extend(String word) {
        return childs.get(word);
    }

    @Override
    public String toString() {
        return path;
    }
}
