package ir.ac.iust.dml.kg.resource.extractor.readers.test;

import ir.ac.iust.dml.kg.resource.extractor.*;
import ir.ac.iust.dml.kg.resource.extractor.ahocorasick.AhoCorasickResourceExtractor;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromKGStoreV2Service;
import ir.ac.iust.dml.kg.resource.extractor.tree.TreeResourceExtractor;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Test entity reader
 */
@SuppressWarnings("Duplicates")
public class MainTest {
    @Test
    public void test() throws Exception {
        IResourceExtractor extractor = new AhoCorasickResourceExtractor();
        try (IResourceReader reader = new ResourceCache("H:\\cache", true)) {
            extractor.setup(reader, 10000);
        }
        extractor.search(" قانون اساسی ایران ماگدبورگ زادگاه حسن روحانی", true, true)
            .forEach(System.out::println);
    }

    @Test
    public void cache() throws Exception {
        final String baseUrl = "http://localhost:8091/";
        final Path tempPath = Paths.get("test");
        if(!Files.exists(tempPath)) Files.createDirectories(tempPath);
        final ResourceCache cache = new ResourceCache(tempPath.toString());
        final List<Resource> allResources = new ArrayList<>();
        final List<Resource> allCachedResource = new ArrayList<>();
        try (IResourceReader reader = new ResourceReaderFromKGStoreV2Service(baseUrl)) {
            cache.cache(reader, 10000);
        }
        try (IResourceReader reader = new ResourceReaderFromKGStoreV2Service(baseUrl)) {
            while (!reader.isFinished()) allResources.addAll(reader.read(10000));
        }
        while (!cache.isFinished()) allCachedResource.addAll(cache.read(10000));
        assert allResources.size() == allCachedResource.size();
        for (int i = 0; i < allResources.size(); i++) {
            assert Objects.equals(allResources.get(i).getIri(), allCachedResource.get(i).getIri());
            assert Objects.equals(allResources.get(i).getInstanceOf(), allCachedResource.get(i).getInstanceOf());
            assert Objects.equals(allResources.get(i).getLabel(), allCachedResource.get(i).getLabel());
            assert Objects.equals(allResources.get(i).getType(), allCachedResource.get(i).getType());
            assert Objects.equals(allResources.get(i).getVariantLabel(), allCachedResource.get(i).getVariantLabel());
            assert Objects.equals(allResources.get(i).getClassTree(), allCachedResource.get(i).getClassTree());
        }
        Files.walk(tempPath).map(Path::toFile).peek(System.out::println).forEach(File::delete);
    }

    @Test
    public void resourceExtractor() throws Exception {
        final IResourceExtractor re = new TreeResourceExtractor();
        re.setup(new IResourceReader() {
            boolean finished = false;

            @Override
            public List<Resource> read(int pageSize) throws Exception {
                finished = true;
                final List<Resource> r = new ArrayList<>();
                r.add(new Resource("http://hossein", "حسین", "محمد حسین", "حسین خادمی خالدی"));
                r.add(new Resource("http://hossein2", "حسین خادمی", "حسین خادمی خالدی"));
                return r;
            }

            @Override
            public Boolean isFinished() {
                return finished;
            }

            @Override
            public void close() throws Exception {

            }
        }, 0);
        final List<MatchedResource> x = re.search("محمد حسین خادمی خالدی", false, false);
        x.forEach(System.out::println);
    }

    @Test
    public void labelConverter() throws Exception {
        final IResourceExtractor re = new TreeResourceExtractor();
        re.setup(new IResourceReader() {
            boolean finished = false;

            @Override
            public List<Resource> read(int pageSize) throws Exception {
                finished = true;
                final List<Resource> r = new ArrayList<>();
                r.add(new Resource("http://hossein", "حسین (64)"));
                return r;
            }

            @Override
            public Boolean isFinished() {
                return finished;
            }

            @Override
            public void close() throws Exception {

            }
        }, label -> {
            final HashSet<String> newLabels = new HashSet<>();
            newLabels.add(label);
            newLabels.add(label.replaceAll("\\(.*\\)", "").trim());
            return newLabels;
        }, 0);
        final List<MatchedResource> x = re.search("حسین", false, false);
        x.forEach(System.out::println);
    }

    @Test
    public void ahoCorasickResourceExtractor() throws Exception {
        final IResourceExtractor re = new AhoCorasickResourceExtractor();
        re.setup(new IResourceReader() {
            boolean finished = false;

            @Override
            public List<Resource> read(int pageSize) throws Exception {
                finished = true;
                final List<Resource> r = new ArrayList<>();
                r.add(new Resource("http://ac", "a c"));
                r.add(new Resource("http://c", "c"));
                r.add(new Resource("http://ca", "c a"));
                r.add(new Resource("http://abb", "a b b"));
                r.add(new Resource("http://b", "b"));
                r.add(new Resource("http://bb", "b b"));
                return r;
            }

            @Override
            public Boolean isFinished() {
                return finished;
            }

            @Override
            public void close() throws Exception {

            }
        }, 0);
        final List<MatchedResource> x = re.search("a c a b b", true, false);
        x.forEach(System.out::println);
    }

    @Test
    public void speedTest() throws Exception {
        final List<String> lines = new ArrayList<>();
        final List<Integer> count = new ArrayList<>();
        Files.readAllLines(Paths.get("h:\\top-queries-both-logs.csv")).stream().skip(1).forEach(l -> {
            String[] args = l.split(",");
            if (args.length == 2) {
                lines.add(args[0].replace("\"", ""));
                count.add(Integer.parseInt(args[1].replace("\"", "")));
            } else
                System.err.println("Bag log:" + l);
        });
        long setupTime = System.currentTimeMillis();
        IResourceExtractor extractor = new TreeResourceExtractor();
        try (IResourceReader reader = new ResourceCache("H:\\cache", true)) {
            extractor.setup(reader, 1000);
        }
        setupTime = System.currentTimeMillis() - setupTime;
        System.out.println("Setup time: " + setupTime);
        int totalCount = 0;
        double queryTime = System.currentTimeMillis();
        for (int i = 0; i < lines.size(); i++) {
            final int c = count.get(i);
            final String l = lines.get(i);
            //Repeat query by it count
            for (int j = 0; j < c; j++) {
                extractor.search(l, false, false);
            }
            totalCount += c;
        }
        queryTime = System.currentTimeMillis() - queryTime;
        System.out.println("Query time: " + queryTime / totalCount);
        System.out.println(totalCount);
    }

    @Test
    public void speedTest2() throws Exception {
        final List<String> words = new ArrayList<>();
        Files.readAllLines(Paths.get("h:\\top-queries-both-logs.csv")).stream().skip(1).forEach(l -> {
            String[] args = l.split(",");
            if (args.length == 2) {
                Collections.addAll(words, args[0].replace("\"", "").split("\\s", -1));
            }
        });
        long setupTime = System.currentTimeMillis();
        IResourceExtractor extractor = new AhoCorasickResourceExtractor();
        try (IResourceReader reader = new ResourceCache("H:\\cache", true)) {
            extractor.setup(reader, 1000);
        }
        setupTime = System.currentTimeMillis() - setupTime;
        System.out.println("Setup time: " + setupTime);
        double[][] queryTime = new double[1000][];
        for (int i = 0; i < queryTime.length; i++) {
            final StringBuilder strBuilder = new StringBuilder();
            for (int j = 0; j <= i; j++)
                strBuilder.append(words.get(j)).append(" ");
            final String sentence = strBuilder.toString();
            queryTime[i] = new double[5];
            for (int k = 0; k < queryTime[i].length; k++) {
                queryTime[i][k] = System.currentTimeMillis();
                for (int z = 0; z < 20; z++)
                    extractor.search(sentence, false, false);
                queryTime[i][k] = (System.currentTimeMillis() - queryTime[i][k]) / 20;
            }
            Arrays.sort(queryTime[i]);
            System.out.println("" + i + "\t:" + queryTime[i][2]);
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter("d:\\res.out"))) {
            for (int i = 0; i < queryTime.length; i++) {
                br.write("" + queryTime[i][2]);
                br.newLine();
            }

        }
    }


}
