package ir.ac.iust.dml.kg.log;

import com.google.common.util.concurrent.AtomicLongMap;
import ir.ac.iust.dml.kg.resource.extractor.*;
import ir.ac.iust.dml.kg.resource.extractor.tree.TreeResourceExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by ali on 17/02/17.
 */
public class LogReader {
    static final Logger LOGGER = LoggerFactory.getLogger(LogReader.class);
    private static Properties properties = new Properties();

    public static void main(String[] args) throws Exception {

        try {
            properties.load(LogReader.class.getResourceAsStream("/config.properties"));
        } catch (Exception e) {
            System.err.println("Cannot load configuration file: " + e.getStackTrace());
        }

        String logFileName = properties.getProperty("log.file.path", "data/queries_filtered.csv");
        IResourceExtractor extractor = setupNewExtractor();

        //Extract queries with Freq
        List<QueryRecord> queryRecords = Files.lines(Paths.get(logFileName))
                .parallel()
                .skip(1)
                .map(l -> LogRecordParser.ParseLine(l))
                .filter(lr -> lr != null)
                .collect(Collectors.toList());

        //Extract queriesFreq
        Map<String, Long> queriesFreq = new HashMap<>();
        queryRecords.forEach(lr -> queriesFreq.put(lr.getQueryText(), lr.getFreq() + queriesFreq.getOrDefault(lr.getQueryText(), 0l)));
        Utils.persistSortedMap(queriesFreq, "results/queriesFreq.txt");

        Map<String, Long> entityFreqsAllClasses = new HashMap<>();
        Map<String, Long> entityFreqsInstanceOf = new HashMap<>();
        Map<String, Long> propertyFreqs = new HashMap<>();
        Map<String, Long> entityFreqs = new HashMap<>();

        Map<String, Long> classFreqsOfNonMappedClasses = new HashMap<>();

        BufferedWriter writer = Files.newBufferedWriter(Paths.get("results/analysis.txt"));

        AtomicLongMap<String> entityPropertyFreqs = AtomicLongMap.create();
        AtomicLongMap<String> entityPropertyFreqsInstanceOfOnly = AtomicLongMap.create();


        for (QueryRecord lr : queryRecords) {
            try {
                    /*lr.setMatchedEntities(extractor.search(lr.getQueryText(), true));*/
                writer.write("\n\n QUERY:\t" + lr.getQueryText() + "\n");
              List<MatchedResource> result = extractor.search(lr.getQueryText(), true, false);
                if (result.size() > 0)
                    System.out.printf("%s:\t result size %d\n", lr.getQueryText(), result.size());

                Set<Resource> entities = new LinkedHashSet<Resource>();
                Set<Resource> properties = new LinkedHashSet<Resource>();

                for (MatchedResource mR : result) {
                    //if (mR.getResource() == null) continue;
                    //Resource mainResource = mR.getResource();

                    Set<String> propertyClassTrees = new HashSet<>();
                    Set<String> entityClassTrees = new HashSet<>();

                    List<Resource> resourcesList = new ArrayList<Resource>();
                    if(mR.getResource()!= null) resourcesList.add(mR.getResource());
                   /* if(mR.getAmbiguities()!=null)
                        for (Resource resource:mR.getAmbiguities())
                            resourcesList.add(resource);*/

                    for(Resource resource: resourcesList){
                        writer.write("\tResource label:\t" + noD(resource.getLabel()) + "\n");
                        writer.write("\t\tiri:\t" + noD(resource.getIri()) + "\n");
                        writer.write("\t\tinstanceOf:\t" + noD(resource.getInstanceOf()) + "\n");
                        writer.write("\t\tclassTree:\t" + noD(resource.getClassTree()) + "\n");
                        writer.write("\t\tvariantLabel:\t" + noD(resource.getVariantLabel()) + "\n");
                        //writer.write("\t\tdisambiguatedFrom:\t" + noD(resource.getDisambiguatedFrom()) + "\n");
                        writer.write("\t\tType:\t" + noD(resource.getType()) + "\n");

                        if(resource.getType() == null){
                            System.err.printf("Unknown resource type: \"%s\" \t URI: %s\n", noD(resource.getLabel()),noD(resource.getIri()));
                            continue;
                        }
                        switch(resource.getType()){
                            case Entity:
                                entities.add(resource);
                                entityFreqs.put(resource.getIri(), lr.getFreq() + entityFreqs.getOrDefault(resource.getIri(), 0l));
                                //count all classes
                                if (resource.getClassTree() != null) {
                                    for (String cls : resource.getClassTree()) {
                                        entityFreqsAllClasses.put(cls, lr.getFreq() + entityFreqsAllClasses.getOrDefault(cls, 0l));
                                    }
                                }
                                //count instanceOf classes
                                if (resource.getInstanceOf() != null)
                                    entityFreqsInstanceOf.put(resource.getInstanceOf(), lr.getFreq() + entityFreqsInstanceOf.getOrDefault(resource.getInstanceOf(), 0l));

                                break;
                            case Property:
                                properties.add(resource);
                                propertyFreqs.put(resource.getIri(), lr.getFreq() + propertyFreqs.getOrDefault(resource.getIri(), 0l));
                                break;
                            default:
                                System.err.printf("Unknown resource type: \"%s\" \t URI: %s\n", noD(resource.getLabel()),noD(resource.getIri()));
                        }
                    }


                    /*if (mR.getResource().getClassTree() != null) {
                        for (String cls : mR.getResource().getClassTree()) {
                            entityFreqsAllClasses.put(cls, lr.getFreq() + entityFreqsAllClasses.getOrDefault(cls, 0l));
                            //if(!Arrays.stream(mQ.getClassTree()).anyMatch(a -> a.equals("Thing")))
                            //    typesFreqOfNonValidTypes.put(cls, lr.getFreq() + typesFreqOfNonValidTypes.getOrDefault(cls, 0l));

                            //Counting patterns
                            if (mR.getResource().getType() != null) {
                                String type = clean(mR.getResource().getType().toString(), "#");
                                if (type.contains("Property"))
                                    propertyClassTrees.add(clean(cls, "/"));
                                else if (type.equals("Resource"))
                                    entityClassTrees.add(clean(cls, "/"));

                            }
                        }
                    }*/
                }
                    /*for (String entityClass : entityClassTrees)
                        for (String propertyClass : propertyClassTrees)
                            entityPropertyFreqs.addAndGet(entityClass + "," + propertyClass, lr.getFreq());*/

                for(Resource entity: entities){
                    for(Resource property: properties){
                        if(entity.getClassTree()!=null && property.getClassTree()!=null){
                            for (String propClass :property.getClassTree()){
                                if(entity.getClassTree().contains(propClass)){
                                    entityPropertyFreqs.addAndGet(property.getIri() + "\t" + propClass, lr.getFreq());
                                }
                                if(entity.getInstanceOf()!=null && entity.getInstanceOf().equals(propClass))
                                    entityPropertyFreqsInstanceOfOnly.addAndGet(property.getIri() + "\t" + propClass, lr.getFreq());
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        writer.close();
        Utils.persistSortedMap(entityFreqsAllClasses, "results/entityFreqsAllClasses.txt");
        Utils.persistSortedMap(entityFreqsInstanceOf, "results/entityFreqsInstanceOf.txt");
        Utils.persistSortedMap(entityPropertyFreqs.asMap(), "results/entityPropertyFreqs.txt");
        Utils.persistSortedMap(entityPropertyFreqsInstanceOfOnly.asMap(), "results/entityPropertyFreqsInstanceOfOnly.txt");
        Utils.persistSortedMap(propertyFreqs, "results/propertyFreqs.txt");
        Utils.persistSortedMap(entityFreqs, "results/entityFreqs.txt");

        //        Utils.persistSortedMap(typesFreqOfNonValidTypes, "results/typesFreqOfNonValidTypes.txt");

            /*Map<String,Long> entitiesFreq = new HashMap<>();
            int num=0;
            for(QueryRecord lR : queryRecords)
                num += lR.getMatchedEntities().size();
                //System.out.println(lR.getQueryText() + " ==> " + lR.getMatchedEntities().size());
                //lR.getMatchedEntities().forEach(entity -> entitiesFreq.put(entity.getEntity(), lR.getFreq() + entitiesFreq.getOrDefault(entitiesFreq.get(entity.getEntity()),0l)));
                //lR.getMatchedEntities().forEach(System.out::println);
            System.out.println("total: " + num);


            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("Entity_freqs.txt"))) {
                for(Map.Entry<String,Long> pair : entitiesFreq.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList()))
                    writer.write(pair.getKey() + "\t" + pair.getValue() + "\n");
            }*/

            /*try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("Queries_with_Entities.txt"))) {

                Iterator<Map.Entry<String, Long>> itr = queriesFreq.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).iterator();

                while(itr.hasN  ext()){
                    Map.Entry<String, Long> e = itr.next();
                    writer.write(String.format("Query: \"%s\" x %d times\n", e.getKey(), e.getValue()));

                    List<Entity> matchedEntities = extractor.search(e.getKey(),true);
                    for (Entity detectedEntity : matchedEntities) {
                        entitiesFreq.put(detectedEntity.toString(), entitiesFreq.getOrDefault(detectedEntity.toString(),0l) + e.getValue());
                        writer.write(String.format("\tEntity: %s\n", detectedEntity.getEntity()));
                    }
                    writer.write("\n");

                    queriesFreq.entrySet()
                            .stream()
                            .sorted(Map.Entry.<String, Long>comparingByValue().reversed());
                }
            }*/
    }

    private static IResourceExtractor setupNewExtractor() throws Exception {
        IResourceExtractor extractor = new TreeResourceExtractor();
        //try (IResourceReader reader = new ResourceReaderFromKGStoreV1Service("http://194.225.227.161:8091/")) {
        long t1 = System.currentTimeMillis();
        try (IResourceReader reader = new ResourceCache("cache", true)) {
            extractor.setup(reader, 1000);
            System.out.println("" + (System.currentTimeMillis() - t1));
            /*System.out.println("testing: قانون اساسی ایران ماگدبورگ");
            extractor.search(" قانون اساسی ایران ماگدبورگ", true).forEach(System.out::println);*/
//            List<MatchedResource> a = extractor.search("هاشمی رفسنجانی", true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return extractor;
    }

    /**
     * Tokenizes the string using delimiter and return the last part.+
     * @param typeIri
     * @param separator
     * @return
     */
    private static String clean(String typeIri, String separator) {
        if (typeIri.contains("#"))
            return typeIri.substring(typeIri.lastIndexOf(separator) + 1);
        return typeIri;
    }

    /**
     * returns value, or "null" if null
     *
     * @param input
     */
    private static String noD(Object input) {
        if (input == null)
            return "null";
        return input.toString();
    }
}