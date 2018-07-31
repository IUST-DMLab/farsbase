package ir.ac.iust.dml.kg.search.logic;

import com.ghasemkiani.util.icu.PersianCalendar;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.text.DateFormat;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.search.logic.data.DataValue;
import ir.ac.iust.dml.kg.search.logic.data.DataValues;
import ir.ac.iust.dml.kg.search.logic.data.ResultEntity;
import ir.ac.iust.dml.kg.search.logic.data.Triple;
import ir.ac.iust.dml.kg.search.logic.recommendation.Recommendation;
import ir.ac.iust.dml.kg.search.logic.recommendation.RecommendationLoader;
import javafx.util.Pair;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by ali on 4/16/17.
 */
public class KGFetcher {
    private static final String KB_PREFIX = "http://fkg.iust.ac.ir/resource/";
    private static Map<String,String> interns = new ConcurrentHashMap<>(7*1000*1000);
    public Multimap<String, Triple> subjTripleMap = HashMultimap.create(2300*1000,1);
    public Multimap<String, Triple> objTripleMap = HashMultimap.create(2300*1000,1);

    public Map<String, Recommendation[]> getRecommendationsMap() {
        return recommendationsMap;
    }
    HashSet<String> predicateBlackList = new HashSet<>(Arrays.asList("http://dublincore.org/2012/06/14/dcterms#subject", "http://fkg.iust.ac.ir/ontology/variantLabel",
            "http://fkg.iust.ac.ir/ontology/wikiPageRedirects","http://fkg.iust.ac.ir/ontology/abstract",
            "http://fkg.iust.ac.ir/ontology/latitudeDirection", "http://fkg.iust.ac.ir/ontology/longitudeDirection"));
    List<String> subjObjBlacklistedPrefixes = Arrays.asList("http://fkg.iust.ac.ir/category/");


    private Map<String, Recommendation[]> recommendationsMap = null;


    public double getRank(String uri){
        double rank = 0;

        if(uri == null || !uri.startsWith("http://"))
            return 0;

        if(subjTripleMap.containsKey(uri))
            rank += subjTripleMap.get(uri).size();

        if(objTripleMap.containsKey(uri))
            rank += objTripleMap.get(uri).size();

        return rank;
    }

    public KGFetcher() throws IOException, SQLException {
        System.err.println("Loading KGFetcher...");
        long t1 = System.currentTimeMillis();
        final String virtuosoServer = ConfigReader.INSTANCE.getString("virtuoso.address", "localhost:1111");
        final String virtuosoUser = ConfigReader.INSTANCE.getString("virtuoso.user", "user_e_SHOMA");
        final String virtuosoPass = ConfigReader.INSTANCE.getString("virtuoso.password", "password_e_SHOMA");
        //graph = new VirtGraph("http://fkg.iust.ac.ir/new", "jdbc:virtuoso://" + virtuosoServer, virtuosoUser, virtuosoPass);
        //model = ModelFactory.createModelForGraph(graph);
        loadFromTTL(ConfigReader.INSTANCE.getString("searcher.ttl.dir", "ttls"));
        System.err.println("Loading recommendations");
        //ignoring recommendation loading errors in IDE debug mode.
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        if (isDebug) {
            try {
                recommendationsMap = RecommendationLoader.read();
            } catch (Exception ignored) {
            }
        } else recommendationsMap = RecommendationLoader.read();
        System.err.printf("KGFetcher loaded in %,d ms\n", (System.currentTimeMillis() - t1));

        //clean up memory
        interns.clear();
        interns = new ConcurrentHashMap<>();
    }

//    public String fetchLabel(String uri, boolean filterNonPersian) {
//        String queryString =
//                "SELECT ?o \n" +
//                        "WHERE {\n" +
//                        "<" +
//                        uri +
//                        "> <http://www.w3.org/2000/01/rdf-schema#label> ?o. \n";
//        if (filterNonPersian)
//            queryString += "FILTER (lang(?o) = \"fa\")";
//
//        queryString += "}";
//    }

//    public String fetchWikiPage(String uri) {
//        String queryString =
//                "SELECT ?o \n" +
//                        "WHERE {\n" +
//                        "<" +
//                        uri +
//                        "> <http://fkg.iust.ac.ir/ontology/wikipageredirects> ?o. \n" +
//                        "}";
//    }

    /**s
     * Returns the (uri,(label,keyValsMap)) pairs for each object statisfying subj-property-object-
     * @param subjectUri
     * @param propertyUri
     * @param searchDirection
     * @return
     */
    public Map<String, Pair<String,Map<String, DataValues>>> fetchSubjPropObjQuery(String subjectUri, String propertyUri, SearchDirection searchDirection) {
        Map<String, Pair<String,Map<String, DataValues>>> matchedObjectLabels = new TreeMap<>();
       /* String[] queryStrings = new String[2];
        queryStrings[0] =
                "SELECT ?o " + //?l " +
                        "WHERE {\n" +
                        "<" +
                        subjectUri +
                        "> <" + propertyUri + "> ?o. \n" +
                        //"?o <http://www.w3.org/2000/01/rdf-schema#label> ?l\n" +
                        //"FILTER (lang(?o) = \"fa\")" +
                        "}";
        queryStrings[1] =   //reverse relation
                "SELECT ?o " + //?l " +
                        "WHERE {\n" +
                        "?o <" + propertyUri +
                        "> <" + subjectUri +
                        ">. \n" +
                        //"?o <http://www.w3.org/2000/01/rdf-schema#label> ?l\n" +
                        //"FILTER (lang(?o) = \"fa\")" +
                        "}";

        for(String queryString: queryStrings) {
            final Query query = QueryFactory.create(queryString);
            final QueryExecution qexec = QueryExecutionFactory.create(query, model);
            final ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                System.err.println("HEY\nHEY\n  RESULT HAS NEXT!! \n\n HEY!!!!!!!");
                final QuerySolution binding = results.nextSolution();
                final RDFNode o = binding.get("o");
                String objectUri = o.toString();*/
        Set<String> resultUris = new LinkedHashSet<>();
        if(searchDirection == SearchDirection.SUBJ_PROP || searchDirection == SearchDirection.BOTH)
            subjTripleMap.get(subjectUri).stream().filter(t -> t.getPredicate().equals(propertyUri)).map(t -> t.getObject()).collect(Collectors.toSet())
                    .forEach(uri -> matchedObjectLabels.put(Util.cleanText(uri), new Pair<>(getLabel(uri),null)));
        if(searchDirection == SearchDirection.PROP_SUBJ || searchDirection == SearchDirection.BOTH)
            objTripleMap.get(subjectUri).stream().filter(t -> t.getPredicate().equals(propertyUri)).map(t -> t.getSubject()).collect(Collectors.toSet())
                    .forEach(uri -> matchedObjectLabels.put(Util.cleanText(uri), new Pair<>(getLabel(uri),null)));

        //Collections-based retrieval
        int relationCounter = 1;
        while(true) {
            String subjectRelatedUri = subjectUri + "/relation_" + relationCounter++;
            if(!subjTripleMap.containsKey(subjectRelatedUri))
                break;
            if (searchDirection == SearchDirection.SUBJ_PROP || searchDirection == SearchDirection.BOTH) {
                Set<String> relationBasedResults = (subjTripleMap.get(subjectRelatedUri)
                        .stream()
                        .filter(t -> t.getPredicate().equals(propertyUri))
                        .map(t -> t.getObject())
                        .collect(Collectors.toSet()));

                // KVs (comments) for each result is created here.
                for(String relationBasedResult : relationBasedResults){
                    System.err.printf("Generating info for relational result: (subj: %s ,\t property: %s , result: %s)\n", subjectRelatedUri, propertyUri, relationBasedResult);
                    Map<String, DataValues> infoKV = new HashMap<>();

                    List<Triple> otherTriplesForThisCollectionalResult = subjTripleMap.get(subjectRelatedUri)
                            .stream()
                            .filter(t -> !(t.getPredicate().contains("22-rdf-syntax-ns#type") || t.getPredicate().contains("/mainPredicate")))
                            .collect(Collectors.toList());

                    for (Triple t : otherTriplesForThisCollectionalResult){
                        System.err.printf("\t\tRelational Result: The info pair for collection is: subj: %s \t pred: %s \t obj: %s\n",t.getSubject(),t.getPredicate(),t.getObject());

                        // Find label of predicate, if any
                        String predLabel = getLabel(t.getPredicate());

                        // Find label of object, if any
                        String objLabel = getLabel(t.getObject());

                        infoKV.put(predLabel,new DataValues(new DataValue(objLabel, t.getObject().startsWith("http")? t.getObject() : "" )));
                    }
                    matchedObjectLabels.put(Util.cleanText(relationBasedResult), new Pair<>(getLabel(relationBasedResult),infoKV));
                }
            }
            //TODO plan: Implement collections-based retrieval for reverse direction (if it makes sense).
        }
        System.err.println("Number of matched results: " + matchedObjectLabels.size() );
        return matchedObjectLabels;
    }


    /**
     * Returns the [persian] label of a data instance.
     * @param uri The input, which is not necessarily a URI.
     * @return
     */
    public String getLabel(String uri) {
        if(uri.contains("("))
            return Util.iriToLabel(uri);


//       //Fetch label from resource extractor
//        String resourceExtractorLabel = new String();
//        try {
//            resourceExtractorLabel = Searcher.getInstance().getExtractor().getResourceByIRI(uri).getLabel();
//        } catch (Exception e) {
//            System.err.println("\t\t\tgetLabel(): No lablel found in ResourceExtractor for: " + uri);
//        }
//        if (resourceExtractorLabel == null || resourceExtractorLabel.isEmpty()) {
//            System.err.println("\t\t\tgetLabel():  for \"" + uri + "\" fetched from resourceExtractor is null/empty, Trying TTL data");
//        }else {
//            System.err.println("\t\t\tgetLabel(): Got label \"" + Util.cleanText(resourceExtractorLabel) +  "\" from resourceExtractor for uri: " + uri);
//            return Util.cleanText(resourceExtractorLabel);
//        }

        //Fetch label from TTL data
        if(subjTripleMap.containsKey(uri)) {
            List<String> labels = subjTripleMap.get(uri).stream().filter(v -> v.getPredicate().equals("http://www.w3.org/2000/01/rdf-schema#label")).map(v -> v.getObject()).collect(Collectors.toList());

            //Return a persian label
            for(String label: labels)
                if(Util.textIsPersian(label) || label.contains("@fa")) {
                    System.err.println("\t\t\tgetLabel(): Got labelFound a persian label for \"" + uri + "\" in TTLs: " + label);
                    return Util.cleanText(label);
                }else {
                    System.err.println("\t\t\tgetLabel(): For " + uri + "\t value \"" + label + "\" is not persian.");
                }

            //Otherwise, return any available label:
            System.err.println("");
            if(!labels.isEmpty())
                return Util.cleanText(labels.get(0));
        }

        //Fetch label from URI
        return Util.iriToLabel(uri);
    }

//    public long fetchsubjPropertyObjRecords(long page, long pageSize) {
//        String queryString =
//                "SELECT ?s ?p ?o\n" +
//                        "WHERE {\n" +
//                        "?s ?p ?o .\n" +
//                        "filter(regex(str(?p), \"/ontology/\") || regex(str(?p), \"/property/\") )\n" +
//                        "}" +
//                        ((page >= 0 && pageSize >= 0) ? " OFFSET " + (page * pageSize) + " LIMIT " + pageSize : "");
//    }

    public void loadFromTTL(String folderPath) throws IOException {
        System.err.println("Loading TTLs from: " + folderPath);
        File folder = new File(folderPath);
        File[] files=folder.listFiles();
        Arrays.sort(files);
        final long[] count = {0};
        long t = System.currentTimeMillis();

        Arrays.stream(files).parallel().forEach(file ->
        {
            if(!file.getName().toLowerCase().endsWith("ttl"))
                return; //ignore non-ttl files and folders.

            Model model = ModelFactory.createDefaultModel();
            try {
                model.read(new FileInputStream(file.getAbsolutePath()), null, "TTL");
            } catch (Throwable e) {
                System.err.println("ERROR while loading " + file.getAbsolutePath() + "\t ... Exiting!");
                e.printStackTrace();
//                System.exit(0);
            }
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) {
                Statement stmt = iter.next();

                putTripleInMapsSynchronized(stmt);
                //System.err.printf("%,d\t%s\t%s\t%s\t%s\n", ++count,file.toString(),s,p,o);
                count[0]++;
            }
            if (iter != null) iter.close();
            System.err.printf("Finished loading %s in %,d ms from beginning\n", file.getName(), System.currentTimeMillis() - t);
        });
        System.err.printf("Finished loading %,d triples in %,d ms \n", count[0], System.currentTimeMillis() - t);
        /*serialize(subjTripleMap,"subjTripleMap.data");
        System.err.printf("Finished subjTripleMap serialization in: %,d ms \n", System.currentTimeMillis() - t);
        serialize(objTripleMap,"objTripleMap.data");
        System.err.printf("Finished objTripleMap serialization in: %,d ms \n", System.currentTimeMillis() - t);*/
    }

    private synchronized void putTripleInMapsSynchronized(Statement stmt) {
        if(predicateBlackList.contains(stmt.getPredicate().toString()))
            return;
        if(subjObjBlacklistedPrefixes.stream().anyMatch(prefix -> stmt.getSubject().toString().startsWith(prefix) || stmt.getObject().toString().startsWith(prefix)))
            return;
        String s = intern(stmt.getSubject().toString());
        String p = intern(stmt.getPredicate().toString());
        String o = intern(objectToString(stmt.getObject()));
        Triple triple = new Triple(s,p,o);
        subjTripleMap.put(s,triple);
        if(o.contains("http://"))
            objTripleMap.put(o,triple);
    }

    private Locale faLocale = Locale.forLanguageTag("fa");
    private com.ibm.icu.util.TimeZone tehranZone = com.ibm.icu.util.TimeZone.getTimeZone("Asia/Tehran");
    private final com.ibm.icu.util.Calendar pCal = PersianCalendar.getInstance(tehranZone, faLocale);
    private DateFormat formatter = pCal.getDateTimeFormat(DateFormat.FULL, DateFormat.NONE, faLocale);
    private String objectToString(RDFNode o) {

        if (o instanceof Resource)
            return o.toString();
        else if (o instanceof Literal) {
            final Literal l = (Literal) o;
            if (l.getDatatype() == null) {
                return l.getString();
            } else {
                final Object value = l.getValue();
                if(value != null) {
                    if (value instanceof Long) {
                        return String.valueOf(value);
                    } else if (value instanceof Integer) {
                        return String.valueOf(value);
                    } else if (value instanceof Short) {
                        return String.valueOf(value);
                    } else if (value instanceof Double) {
                        return String.valueOf(value);
                    } else if (value instanceof Boolean) {
                        return String.valueOf(value);
                    } else if (value instanceof Byte) {
                        return String.valueOf(value);
                    } else if (value instanceof Float) {
                        return String.valueOf(value);
                    } else if (l.getValue() instanceof XSDDateTime) {
                        synchronized (pCal) {
                            final Calendar cal = ((XSDDateTime) (l.getValue())).asCalendar();
                            pCal.setTimeInMillis(cal.getTimeInMillis());
                            return formatter.format(pCal);
                        }
                    } else {
                        return l.getString();
                    }
                }
            }
        }
        return o.toString();
    }

    private void serialize(Object obj, String filePath) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(obj);
        out.close();
        fileOut.close();
    }

    /**
     * String deduplication.
     * @param str
     * @return
     */
    public static String intern(String str) {
        if(!interns.containsKey(str)) {
            interns.put(str,str);
        }
        return interns.get(str);
    }



    public static void main(String[] args) throws IOException, SQLException {
        KGFetcher fetcher = new KGFetcher();
        fetcher.loadFromTTL(args[0]);
//        KGFetcher.loadRecommendations("C:\\Users\\ali\\Downloads\\recommendations2_sample.json");
    }

    public static void loadRecommendations(String path) throws IOException {
        List<String> texts = Files.readAllLines(Paths.get(path));
        for(String t : texts) {
            JsonElement jelement = new JsonParser().parse(t);
            JsonObject jobject = jelement.getAsJsonObject();
            System.err.println();
        }
    }

    public List<String> fetchPhotoUrls(String link) {
        List<String> photos = new ArrayList<>();
        try{
            subjTripleMap.get(link).stream()
                    .filter(t -> t.getPredicate().equals("http://fkg.iust.ac.ir/ontology/picture"))
                    .forEach(t -> photos.add(t.getObject()));
        }catch(Exception e){
            e.printStackTrace();
        }
        return photos;
    }

    public Multiset<String> getRecommendationsUri(String uri) {
        System.err.println("Computing recommendations for " + uri);
        Set<String> neighbors = getNeighbors(uri);
        Multiset<String> relevants = HashMultiset.create();
        relevants.addAll(neighbors);
        for(String nb : neighbors){
            relevants.addAll(getNeighbors(nb));
        }
        return relevants;
    }

    public Set<String> getNeighbors(String uri) {
        int LIMIT = 100;
        List<ResultEntity> resultEntities = new ArrayList<>();
        List<String> triples = new ArrayList<>();
        triples.addAll(subjTripleMap.get(uri).stream().limit(LIMIT).map(t -> t.getObject()).collect(Collectors.toSet()));
        triples.addAll(objTripleMap.get(uri).stream().limit(LIMIT).map(t -> t.getSubject()).collect(Collectors.toSet()));
        Set<String> result = triples.stream().filter(s -> s.contains("/resource/")).filter(s -> !s.equals(uri)).collect(Collectors.toSet());
        System.err.printf("Neighbors for %s \t =  %d\n", uri, result.size() );
        return result;
    }
}