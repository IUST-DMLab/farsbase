package ir.ac.iust.dml.kg.raw.distantsupervison;

/**
 * Created by hemmatan on 4/5/2017.
 */
public class Constants {

    public static class sentenceAttribs {
        public static final String RAW = "raw";
        public static final String NORMALIZED = "normalized";
        public static final String WORDS = "words";
        public static final String POSTAG = "postag";
        public static final String OBJECT_ABV = "$OBJ";
        public static final String SUBJECT_ABV = "$SUBJ";
    }

    public static class patternAttribs {
        public static final String RELATION_NAME = "relation_name";
        public static final String FREQUENCY = "frequency";
        public static final String PATTERN = "pattern";
        public static final String OBJECT_ENTITY_TYPE = "object_entity_type";
        public static final String SUBJECT_ENTITY_TYPE = "subject_entity_type";
    }

    public static class bagOfWordsAttribs {
        public static final String TF = "tf";
        public static final String IDF = "idf";
        public static final String TF_IDF = "tf_idf";
        public static final String TOKEN = "token";
    }

    public static class segmentedBagOfWordsAttribs {
        public static final String SUBJECT_PRECEDING = "subjectPreceding.model";
        public static final String OBJECT_PRECEDING = "objectPreceding.model";
        public static final String SUBJECT_FOLLOWING = "subjectFollowing.model";
        public static final String OBJECT_FOLLOWING = "objectFollowing.model";
    }

    public static class corpusDbEntryAttribs {
        public static final String GENERALIZED_SENTENCE = "generalized_sentence";
        public static final String OCCURRENCE = "occurrence";
        public static final String SUBJECT = "subject";
        public static final String OBJECT = "object";
        public static final String SUBJECT_TYPE = "subject_type";
        public static final String OBJECT_TYPE = "object_type";
        public static final String PREDICATE = "predicate";
        public static final String OBJECT_HEAD = "object_head";
        public static final String SUBJECT_HEAD = "subject_head";
    }

    public static class entityModelAttribs {
        public static final String PREFIX = "http://fkg.iust.ac.ir/ontology/"; // "http://fkg.iust.ac.ir/ontology/"
    }

    public static class trainingSetModes {
        public static final String USE_ALL_PREDICATES_IN_EXPORTS_JSON = "useAllPredicatesInExportsJson";
        public static final String LOAD_PREDICATES_FROM_FILE = "loadPredicatesFromFile";
        public static final String LOAD_CORPUS_FREQUENT_PREDICATES = "loadCorpusFrequentPredicates";
    }

    public static class triplesAttribs {
        public static final String SUBJECT = "subject";
        public static final String OBJECT = "object";
        public static final String PREDICATE = "predicate";
        public static final String CONFIDENCE = "confidence";
        public static final String SOURCE = "source";
        public static final String ORIGIN_SENTENCE = "origin_sentence";
    }

    public static class trainCorpusNames {
        public static final String ALL_WIKI_WITHOUT_DEPENDENCY_FEATURES = "corpus";
        public static final String WIKI_PLUS_DEPENDENCY_FEATURES = "corpus_with_dependency";
        public static final String WIKI_PLUS_DEPENDENCY_MINUS_FREQUENT_TRIPLES = "corpus_with_dependency_1_2_copy";
    }

    public static class classifierTypes {
        public static final String SPECIES_SPECIES = "species_species";
        public static final String ATHLETE_SPORTSTEAM = "athlete_sportsteam";
        public static final String WORK_AGENT = "work_agent";
        public static final String PERSON_PERSONFUNCTION = "person_personFunction";
        public static final String PERSON_PERSON = "person_person";
        public static final String PERSON_PLACE = "person_place";
        public static final String THING_PLACE = "thing_place";
        public static final String THING_PERSON = "thing_person";
        public static final String THING_AGENT = "thing_agent";
        public static final String GENERAL = "general";
    }


    public static class runOptions {
        public static final String TRAIN = "train";
        public static final String TEST = "test";
        public static final String MAKE_DB = "makeDB";
    }
}
