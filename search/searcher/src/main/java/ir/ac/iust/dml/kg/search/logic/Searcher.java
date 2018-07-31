package ir.ac.iust.dml.kg.search.logic;

import com.google.common.base.Strings;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.resource.extractor.*;
import ir.ac.iust.dml.kg.resource.extractor.tree.TreeResourceExtractor;
import ir.ac.iust.dml.kg.search.logic.data.DataValues;
import ir.ac.iust.dml.kg.search.logic.data.ResultEntity;
import ir.ac.iust.dml.kg.search.logic.data.SearchResult;
import ir.ac.iust.dml.kg.search.logic.recommendation.Recommendation;
import javafx.util.Pair;
import knowledgegraph.normalizer.PersianCharNormalizer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Searcher {
    private static final String BLACKLIST_FILE_NAME =
        ConfigReader.INSTANCE.getString("searcher.blacklist", "black_list.txt");
    private static final int MAX_RECOMMENDATIONS = 20;
    private final IResourceExtractor extractor;
    private final KGFetcher kgFetcher;
    private static final PersianCharNormalizer normalizer = customizeNormalizer();

    private final Set<String> blacklist = new HashSet<String>();

    private static String semaphore = "Semaphore";
    private static Searcher instance;

    public static Searcher getInstance() throws Exception {
        synchronized (semaphore) {
            if (instance != null) return instance;
            return new Searcher();
        }
    }

    public Searcher() throws Exception {
        instance = this;
        blacklist.addAll(Files.readAllLines(Paths.get(BLACKLIST_FILE_NAME)));
        blacklist.forEach(s -> System.out.println("Blacklist: \"" + s + "\""));
        extractor = setupNewExtractor();
        kgFetcher = new KGFetcher();
    }

    public SearchResult search(String keyword) {
        if(keyword == null || keyword.trim().isEmpty())
            return new SearchResult();

        String queryText = normalizer.normalize(keyword);
        final SearchResult result = new SearchResult();
        System.err.println(new Date() + " PROCESSING QUERY: " + keyword);
        //Answering predicate-subject
        // phrases
        try {
            List<MatchedResource> matchedResourcesUnfiltered = extractor.search(queryText, true,false);

            List<Resource> allMatchedResources = matchedResourcesUnfiltered.stream()
                    .flatMap(mR -> {
                        List<Resource> list = new ArrayList<>();
                        if (mR.getResource() != null)
                            list.add(mR.getResource());
                        if (mR.getAmbiguities() != null)
                            list.addAll(mR.getAmbiguities());

                        //TODO: to be refurbished
                        list = list.stream().map(r ->
                        {
                            if(r.getIri() != null && (r.getIri().contains("/ontology/") || r.getIri().contains("/property/"))
                                    && (r.getType() == null || !r.getType().toString().contains("Property")) )
                                r.setType(ResourceType.Property);
                            return r;
                        }).collect(Collectors.toList());


                        System.err.println("\n\nList Members before filtering:");
                        list.stream().forEach(r -> System.err.println("\t" + r.getIri()));

                        list = list.stream().filter(r -> !blacklist.contains(r.getIri())).collect(Collectors.toList());
                        System.err.println("\n\nList Members before filtering (without blacklisted items):");
                        list.stream().forEach(r -> System.err.println("\t" + r.getIri()));

                        //if there is a detected property, skip all other entities
                        if(list.stream().anyMatch(r -> r.getType() != null && r.getType().toString().contains("Property")))
                            return list.stream().filter(r -> r.getType() != null && r.getType().toString().contains("Property"));
                        return list.stream();
                    })
                    .filter(r -> r.getIri() != null)
                    .filter(r -> !r.getIri().contains("ابهام"))
                    .filter(Util.distinctByKey(Resource::getIri)) //distinct by Iri
                    .filter(r -> !blacklist.contains(r.getIri()))
                    .collect(Collectors.toList());

            System.err.println("\n\nallMatchedResources:");
            allMatchedResources.stream().forEach(r -> System.err.println("\t" + r.getIri()));


            List<Resource> properties = allMatchedResources.stream()
                    .filter(r -> r.getType() != null)
                    .filter(r -> r.getType().toString().contains("Property"))
                    .collect(Collectors.toList());

            System.err.println("\n\nproperties:");
            properties.stream().forEach(r -> System.err.println("\t" + r.getIri()));

            List<Resource> entities = allMatchedResources.stream()
                    .filter(r -> !properties.contains(r))
                    .filter(r -> r.getIri() != null)
                    .filter(Util.distinctByKey(Resource::getIri)) //distinct by Iri
                    .sorted((o1, o2) -> ((Double) kgFetcher.getRank(o2.getIri())).compareTo(kgFetcher.getRank(o1.getIri())))
                    .collect(Collectors.toList());

            System.err.println("\n\nentities:");
            entities.stream().forEach(r -> System.err.println("\t" + r.getIri()));

            // Manual Corrections
            doManualCorrections(properties,queryText);

            for (Resource subjectR : entities) {
                for (Resource propertyR : properties) {
                    try {
                        System.err.println("Trying combinatios for " + subjectR.getIri() + "\t & \t" + propertyR.getIri());
                        Map<String, Pair<String, Map<String, DataValues>>> objectLablesAndKVs = kgFetcher.fetchSubjPropObjQuery(subjectR.getIri(), propertyR.getIri(),selectDirection(subjectR.getIri(),propertyR.getIri(),queryText));
                        System.err.println("\t RESULTS FOUND: " + objectLablesAndKVs.keySet().size());
                        for (Map.Entry<String, Pair<String, Map<String, DataValues>>> olEntry : objectLablesAndKVs.entrySet().stream().sorted((o1, o2) -> ((Double) kgFetcher.getRank(o2.getKey())).compareTo(kgFetcher.getRank(o1.getKey()))).collect(Collectors.toList())) {
                            System.err.printf("Object: %s\t%s\n", olEntry.getKey(), olEntry.getValue());
                            ResultEntity resultEntity = new ResultEntity();
                            if(olEntry.getKey().startsWith("http"))
                                resultEntity.setLink(olEntry.getKey());
                            resultEntity.setReferenceUri(subjectR.getIri());
                            System.err.println("Setting Title:");
                            resultEntity.setTitle(olEntry.getValue().getKey());
                            System.err.println("Setting KV:");
                            if(olEntry.getValue().getValue() != null)
                                resultEntity.setKeyValues(olEntry.getValue().getValue());
                            System.err.println("Setting Description:");
                            resultEntity.setDescription("نتیجه‌ی گزاره‌ای");
                            resultEntity.setPhotoUrls(kgFetcher.fetchPhotoUrls(resultEntity.getLink()));
                            resultEntity.setResultType(ResultEntity.ResultType.RelationalResult);
                            if (!(Strings.isNullOrEmpty(subjectR.getLabel()) || Strings.isNullOrEmpty(propertyR.getLabel()))) {
                                resultEntity.setDescription(resultEntity.getDescription() + ": [" + /*subjectR.getLabel()*/ kgFetcher.getLabel(subjectR.getIri()) + "] / [" + propertyR.getLabel() + "]");
                            }
                            System.err.println("ADDING!");
                            result.getEntities().add(resultEntity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //Output individual entities
            Set<String> uriOfEntities = new HashSet<>();
            for (Resource entity : entities) {
                try {
                    ResultEntity resultEntity = matchedResourceToResultEntity(entity);
                    if (uriOfEntities.contains(purify(resultEntity.getLink())))
                        continue;
                    uriOfEntities.add(purify(resultEntity.getLink()));
                    result.getEntities().add(resultEntity);
                    result.getEntities().addAll(getRecommendations(resultEntity.getLink(),MAX_RECOMMENDATIONS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for(ResultEntity r : result.getEntities()) {
                if (r.getLink() != null && r.getLink().contains(")"))
                    r.setTitle(Util.iriToLabel(r.getLink()));
                if(r.getTitle().contains("/"))
                    r.setTitle(Util.iriToLabel(r.getTitle()));
                /*if(r.getTitle() != null && r.getTitle().contains("(ابهام‌زدایی شده)"))*/

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String purify(String uri){
        return uri.trim().replaceAll("[ ‌_ـ]","");
    }


    public Collection<ResultEntity> getRecommendations(String uri,int max) {
        System.err.println("Searcher: Computing recommendations for: " + uri);
        //Multiset<String> recomUris = kgFetcher.getRecommendationsUri(uri);
        List<ResultEntity> results = new ArrayList<>();

        if(kgFetcher.getRecommendationsMap().containsKey(uri)) {
            for(Recommendation recom : kgFetcher.getRecommendationsMap().get(uri)){
                int count = 0;
                //for(Multiset.Entry<String> weightedUri : Multisets.copyHighestCountFirst(recomUris).entrySet()){
                ResultEntity result = new ResultEntity();
                //String iri = weightedUri.getElement();
                result.setLink(recom.getUri());
                result.setTitle(Util.iriToLabel(recom.getUri()));
                result.setPhotoUrls(kgFetcher.fetchPhotoUrls(recom.getUri()));
                result.setDescription("موجودیت‌های مرتبط با " + Util.iriToLabel(uri));
                result.setResultType(ResultEntity.ResultType.Similar);
                results.add(result);
                if (++count >= max)
                    break;
            }
        }
        return results;
    }


    private SearchDirection selectDirection(String subjectIri, String propertyIri, String queryText) {
        //return SearchDirection.BOTH;
        if(propertyIri.contains("ontology/starring") || propertyIri.contains("ontology/director")  || propertyIri.toLowerCase().contains("ontology/province"))
            return SearchDirection.BOTH;

        return SearchDirection.SUBJ_PROP;
    }

    /**
     *  Adds undetected properties according to the patterns.
     *  TODO: to be developed as an independent module with a look-aside DB
     * @param properties
     * @param queryText
     */
    private void doManualCorrections(List<Resource> properties, String queryText) {
        if((queryText.contains("فیلم") ||  queryText.contains("سریال"))
                && !(queryText.contains("درآمد") || queryText.contains("درامد") ||  queryText.contains("بودجه"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/starring"))) {
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/starring", "فیلم‌"));
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/director","کارگردان"));
        }

        if((queryText.contains("درامد") ||  queryText.contains("درآمد"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/revenue")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/revenue","درآمد"));

        if((queryText.contains("نویسنده") ||  queryText.contains("مولف"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/author")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/author","نویسنده"));

        if((queryText.contains("پیش شماره") ||  queryText.contains("پیش‌شماره"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/areaCode")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/areaCode","کد تلفن"));

        if((queryText.contains("ترکیبات"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/ingredient")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/ingredient","ترکیبات اصلی"));

        if((queryText.contains("کارکنان") || queryText.contains("پرسنل") || queryText.contains("کارمندان"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/numberOfEmployees")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/numberOfEmployees","تعداد پرسنل"));

        if((queryText.contains("باشگاه") || queryText.contains("تیم"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/team")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/team","تیم"));

        if((queryText.contains("ساخت") && queryText.contains("دوره"))
                && properties.stream().noneMatch(r -> r.getIri().contains("property/دیرینگی")))
            properties.add(new Resource("http://fkg.iust.ac.ir/property/دیرینگی","دوره ساخت (دیرینگی)"));

        if((queryText.contains("پلاک"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/vehicleCode")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/vehicleCode","پلاک اتومبیل"));

        if((queryText.contains("بزرگ") && queryText.contains("شهر"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/largestCity")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/largestCity","بزرگترین شهر"));

        if((queryText.contains("مواد لازم") && queryText.contains("ترکیبات"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/ingredient")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/ingredient","ترکیبات اصلی"));


        if((queryText.contains("کتابهای") || queryText.contains("کتب") || queryText.contains("کتاب های"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/notableWork")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/notableWork","تالیفات"));


        if((queryText.contains("شهر های") || queryText.contains("شهرهای"))
                && properties.stream().noneMatch(r -> r.getIri().contains("ontology/province")))
            properties.add(new Resource("http://fkg.iust.ac.ir/ontology/province","شهرهای استان"));

    }

    private ResultEntity matchedResourceToResultEntity(Resource resource) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setTitle(resource.getLabel());

        //resultEntity.setSubtitle(kgFetcher.fetchLabel(resource.getInstanceOf(), true));
        Resource ontologyClass = extractor.getResourceByIRI(resource.getInstanceOf());
        if(ontologyClass != null && ontologyClass.getLabel() != null && !ontologyClass.getLabel().isEmpty())
            resultEntity.setSubtitle(ontologyClass.getLabel());

        resultEntity.setLink(resource.getIri());
                    /*String wikiPage = kgFetcher.fetchWikiPage(resource.getIri());
                    if (wikiPage != null)
                        resultEntity.setLink(wikiPage);*/

        resultEntity.setPhotoUrls(kgFetcher.fetchPhotoUrls(resource.getIri()));

        if (resource.getType() != null) {
            String type = "Type: " + resource.getType().toString();
            if (resource.getType() == ResourceType.Property) {
                type = " (خصیصه)";
                resultEntity.setResultType(ResultEntity.ResultType.Property);
            }
            if (resource.getType() == ResourceType.Entity) {
                type = " (موجودیت)";
                resultEntity.setResultType(ResultEntity.ResultType.Entity);
            }
            if (resultEntity.getSubtitle() == null && !type.equals(""))
                resultEntity.setSubtitle(type);
            else resultEntity.setSubtitle(resultEntity.getSubtitle() + type);
        }
        if (resource.getLabel() == null) {
            resultEntity.setTitle(extractTitleFromIri(resource.getIri()));
        }
        return resultEntity;
    }

    private static String extractTitleFromIri(String iri) {
        return iri.substring(iri.lastIndexOf("/") + 1).replace('_', ' ');
    }

    private static IResourceExtractor setupNewExtractor() throws Exception {
        IResourceExtractor extractor = new TreeResourceExtractor();
        String cacheDirectory = ConfigReader.INSTANCE.getString("searcher.cache.dir", "cache");
        try (IResourceReader reader = new ResourceCache(cacheDirectory, true)) {
            System.err.println("Loading resource-extractor from cache: " + cacheDirectory);
            long t1 = System.currentTimeMillis();
            extractor.setup(reader, 10000);
            //extractor.setup(reader, 1000000);
            System.err.printf("resource-extractor loaded from cache in %,d miliseconds\n", (System.currentTimeMillis() - t1));
        }
        return extractor;
    }
    public IResourceExtractor getExtractor() {
        return extractor;
    }

    private static PersianCharNormalizer customizeNormalizer() {
        List<PersianCharNormalizer.Option> options = new ArrayList<>();

        options.add(PersianCharNormalizer.Option.NORMAL_HE);
        options.add(PersianCharNormalizer.Option.NORMAL_KAF);
        options.add(PersianCharNormalizer.Option.NORMAL_NUMBERS);
        options.add(PersianCharNormalizer.Option.NORMAL_WAW);
        options.add(PersianCharNormalizer.Option.NORMAL_YEH);

        return new PersianCharNormalizer(options);
    }

// this line is for git sync test!

}
