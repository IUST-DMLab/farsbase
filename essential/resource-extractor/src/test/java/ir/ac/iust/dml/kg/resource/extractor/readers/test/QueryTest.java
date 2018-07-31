package ir.ac.iust.dml.kg.resource.extractor.readers.test;

import ir.ac.iust.dml.kg.resource.extractor.*;
import ir.ac.iust.dml.kg.resource.extractor.tree.TreeResourceExtractor;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@SuppressWarnings("Duplicates")
public class QueryTest {
    private final static int COUNT = 50;
    private final static boolean REPEAT_ALLOW = false;
    private final static boolean COUNT_AMBIGUITY = false;
    private final static String CACHE_FOLDER = "/home/khaledi/Cache";
    private final static String OUTPUT_FILE = "/home/khaledi/res.out";
    private final static String PATTERN_FILE = "/home/khaledi/patterns.txt";
    private final static String QA_LOG_FILE = "/home/khaledi/logs.txt";

    private class KeyCount {
        String key;
        Resource resource;
        double count;

        KeyCount(String key, double count) {
            this.key = key;
            this.count = count;
        }

        KeyCount(Resource resource, double count) {
            this.key = resource.getIri();
            this.resource = resource;
            this.count = count;
        }
    }

    private Random rnd = new Random(System.currentTimeMillis());

    @Test
    public void createEntityCount() throws Exception {
        final IResourceExtractor extractor = new TreeResourceExtractor();
        try (IResourceReader reader = new ResourceCache(CACHE_FOLDER, true)) {
            extractor.setup(reader, 0);
        }
        final List<KeyCount> counts = countEntity(extractor);
        try (BufferedWriter br = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            Files.readAllLines(Paths.get(PATTERN_FILE), Charset.forName("utf8")).forEach(l -> {
                final String[] args = l.split("\t");
                if (args.length == 2) {
                    final List<String> excludes = new ArrayList<>();
                    for (int i = 0; i < COUNT; i++) {
                        KeyCount key = getEntity(args[1], counts, excludes);
                        if (key != null) {
                            if (!REPEAT_ALLOW)
                                excludes.add(key.key);
                            try {
                                br.write(args[0]);
                                br.write(" ");
                                br.write(key.resource.getLabel());
                                br.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else
                    System.err.println("Bad format in patterns " + l);
            });
        }
    }

    private List<KeyCount> countEntity(IResourceExtractor extractor) throws IOException {
        final List<KeyCount> sentences = new ArrayList<>();
        Files.readAllLines(Paths.get(QA_LOG_FILE), Charset.forName("utf8")).stream().skip(1).forEach(l -> {
            final String[] args = l.split(",");
            if (args.length == 3) {
                final String key = args[1].replace("\"", "");
                final int count = Integer.parseInt(args[2].replace("\"", ""));
                sentences.add(new KeyCount(key, count));
            }
        });

        final Map<String, KeyCount> resources = new HashMap<>();
        sentences.parallelStream().forEach(s -> {
            final List<MatchedResource> res = extractor.search(s.key, true, false);
            res.forEach(r -> {
                if (r.getResource() != null) {
                    KeyCount current = resources.get(r.getResource().getIri());
                    if (current == null) current = new KeyCount(r.getResource(), 0);

                    current.count += s.count;
                    resources.put(r.getResource().getIri(), current);
                }
                if (COUNT_AMBIGUITY)
                    r.getAmbiguities().forEach(a -> {
                        KeyCount current = resources.get(a.getIri());
                        if (current == null) current = new KeyCount(a, 0);
                        current.count += s.count / r.getAmbiguities().size();
                        resources.put(a.getIri(), current);
                    });
            });
        });
        final List<KeyCount> lists = new ArrayList<>();
        lists.addAll(resources.values());
        lists.sort((o1, o2) -> Double.compare(o2.count, o1.count));
        return lists;
    }

    private KeyCount getEntity(String clazz, List<KeyCount> counts, List<String> excludes) {
        final List<KeyCount> matched = new ArrayList<>();
        counts.forEach(c -> {
            if (!excludes.contains(c.key) && c.resource.getClassTree().contains(clazz))
                matched.add(c);
        });
        if (matched.isEmpty()) {
            System.err.println("There is no candidate for " + clazz);
            return null;
        }
        double sum = 0;
        for (KeyCount k : matched) sum += k.count;
        double chance = rnd.nextDouble() * sum;
        for (KeyCount k : matched) {
            if (k.count > chance)
                return k;
            chance -= k.count;
        }
        throw new RuntimeException();
    }

}
