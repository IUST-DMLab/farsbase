package ir.ac.iust.dml.kg.resource.extractor.ahocorasick;

import ir.ac.iust.dml.kg.resource.extractor.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Use Aho–Corasick algorithm for string matching
 */
public class AhoCorasickResourceExtractor implements IResourceExtractor {
    static final Logger LOGGER = LogManager.getLogger(AhoCorasickResourceExtractor.class);
    private final AllResourceCache cache = new AllResourceCache();
    private final Trie trie = new Trie();

    @Override
    public void setup(IResourceReader reader, int pageSize) throws Exception {
        setup(reader, null, 0);
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
                newLabels.forEach(l -> trie.add(current, l.split("\\s", -1)));
            });
        }
        trie.constructFailureStates();
    }

    @Override
    public List<MatchedResource> search(String text, Boolean removeSubset, Boolean removeCategory) {
        if(removeCategory != null && removeCategory) return null;
        final String[] words = text.split("\\s", -1);
        final List<MatchedResource> resources = trie.parseText(words, removeSubset);
        MatchedResource bestMatch = null;
        for (int j = resources.size() - 1; j >= 0; j--) {
            final MatchedResource current = resources.get(j);
          if (current.getResource().getType() == ResourceType.Category) continue;
            if (bestMatch == null || current.getStart() < bestMatch.getStart())
                bestMatch = current;
            else if (current.getEnd() <= bestMatch.getEnd()) {
                if (removeSubset)
                    resources.remove(j);
                else
                    current.setSubsetOf(bestMatch);
            }
        }
        return resources;
    }

    @Override
    public Resource getResourceByIRI(String iri) {
        return null;
    }
}
