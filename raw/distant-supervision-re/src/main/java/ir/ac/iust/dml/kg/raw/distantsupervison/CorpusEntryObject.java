package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.DependencyParser;
import ir.ac.iust.dml.kg.raw.WordTokenizer;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemmatan on 4/18/2017.
 */
public class CorpusEntryObject {

    private String subjectHead;
    private List<ConcurrentDependencyGraph> parsedSentence = new ArrayList<>();
    private Sentence originalSentence;
    private String generalizedSentence;
    private String object, subject;
    private String objectHead;
    private List<String> objectType, subjectType;
    private String predicate;
    private int occurrence;
    private List<String> allQueryWordsInGeneralizedForm = new ArrayList<>();
    private int subjectIndexInGeneralizedForm = -1;
    private int objectIndexInGeneralizedForm = -1;
    //private

    public CorpusEntryObject() {
    }

    public CorpusEntryObject(Sentence originalSentence, String generalizedSentence, String object, String subject, List<String> objectType, List<String> subjectType, String predicate, int occurrence,
                             String subjectHead, String objectHead) {
        this.originalSentence = originalSentence;
        this.generalizedSentence = generalizedSentence;
        this.object = object;
        this.subject = subject;
        this.objectType = objectType;
        this.subjectType = subjectType;
        this.predicate = predicate;
        this.occurrence = occurrence;

        allQueryWordsInGeneralizedForm = WordTokenizer.tokenize(getGeneralizedSentence());

        this.subjectIndexInGeneralizedForm = allQueryWordsInGeneralizedForm.indexOf(Constants.sentenceAttribs.SUBJECT_ABV);
        this.objectIndexInGeneralizedForm = allQueryWordsInGeneralizedForm.indexOf(Constants.sentenceAttribs.OBJECT_ABV);

        this.objectHead = objectHead;
        this.subjectHead = subjectHead;
    }

    public CorpusEntryObject(Sentence originalSentence, String generalizedSentence, String object, String subject, List<String> objectType, List<String> subjectType, String predicate, int occurrence) {
        this.originalSentence = originalSentence;
        this.generalizedSentence = generalizedSentence;
        this.object = object;
        this.subject = subject;
        this.objectType = objectType;
        this.subjectType = subjectType;
        this.predicate = predicate;
        this.occurrence = occurrence;

        allQueryWordsInGeneralizedForm = WordTokenizer.tokenize(getGeneralizedSentence());

        this.subjectIndexInGeneralizedForm = allQueryWordsInGeneralizedForm.indexOf(Constants.sentenceAttribs.SUBJECT_ABV);
        this.objectIndexInGeneralizedForm = allQueryWordsInGeneralizedForm.indexOf(Constants.sentenceAttribs.OBJECT_ABV);

        this.parsedSentence = DependencyParser.parseRaw(this.originalSentence.getRaw());
        this.objectHead = setEntitysHead(this.object);
        this.subjectHead = setEntitysHead(this.subject);
    }

    public String setEntitysHead(String entity) {
        String[] entityTokens = entity.split(" ");
        String result = "unknownError";
        if (parsedSentence == null) result = "nullParsedSentence";
        else if (parsedSentence.isEmpty()) result = "emptyParsedSentence";
        else {
            int idxi = -1;
            int idxj = -1;
            for (int i = 0; i < parsedSentence.size(); i++) {
                ConcurrentDependencyGraph parsedUnit = parsedSentence.get(i);
                if (parsedUnit == null) continue;
                for (int j = 1; j < parsedUnit.nTokenNodes() + 1; j++) {
                    ConcurrentDependencyNode node = parsedUnit.getDependencyNode(j);
                    if (node != null) {
                        int l = 0;
                        for (int k = j; k < parsedUnit.nTokenNodes() + 1 && l < entityTokens.length; k++, l++) {
                            idxi = i;
                            idxj = j;
                            ConcurrentDependencyNode currentNode = parsedUnit.getDependencyNode(k);
                            if (!currentNode.getLabel("FORM").equalsIgnoreCase(entityTokens[l])) {
                                idxi = -1;
                                idxj = -1;
                                break;
                            }
                        }
                    }
                    if (idxi != -1) break;
                }
                if (idxi != -1) break;
            }
            if (idxi == -1) result = "couldNotFindYourToken";
            else {
                result = parsedSentence.get(idxi).getDependencyNode(idxj).getHead().getLabel("FORM");
            }
        }
        return result;
    }

    public static void setEntityType(List<MatchedResource> result_entity, List<String> entityType) {
        if (result_entity == null || result_entity.size() == 0 || result_entity.get(0).getResource() == null)
            entityType.add("null");
        else if (result_entity.get(0).getResource().getClassTree() == null || result_entity.get(0).getResource().getClassTree().size() == 0)
            entityType.add(result_entity.get(0).getResource().getIri());
        else entityType.addAll(result_entity.get(0).getResource().getClassTree());
    }

    public String getGeneralizedSentence() {
        return generalizedSentence;
    }

    public void setGeneralizedSentence(String generalizedSentence) {

        this.generalizedSentence = generalizedSentence;
        allQueryWordsInGeneralizedForm = WordTokenizer.tokenize(getGeneralizedSentence());

        this.subjectIndexInGeneralizedForm = allQueryWordsInGeneralizedForm.indexOf(Constants.sentenceAttribs.SUBJECT_ABV);
        this.objectIndexInGeneralizedForm = allQueryWordsInGeneralizedForm.indexOf(Constants.sentenceAttribs.OBJECT_ABV);

    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public Sentence getOriginalSentence() {

        return originalSentence;
    }

    public void setOriginalSentence(Sentence originalSentence) {
        this.originalSentence = originalSentence;
        this.parsedSentence = DependencyParser.parseRaw(this.originalSentence.getRaw());
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public List<String> getObjectType() {
        return objectType;
    }

    public void setObjectType(List<String> objectType) {
        this.objectType = objectType;
    }

    public List<String> getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(List<String> subjectType) {
        this.subjectType = subjectType;
    }

    public String toString() {
        return getOriginalSentence().getNormalized()
                + "\t" + this.getSubject()
                + "\t" + this.getObject()
                + "\t" + "predicate: " + this.getPredicate() + "\n";
    }

    public List<String> getSubjectPrecedingWords() {
        List<String> words = new ArrayList<>();

        int startIdx;
        int endIdx = this.subjectIndexInGeneralizedForm;


        if (this.subjectIndexInGeneralizedForm < this.objectIndexInGeneralizedForm) {
            startIdx = (this.subjectIndexInGeneralizedForm - Configuration.maxWindowSize < 0) ? 0 : this.subjectIndexInGeneralizedForm - Configuration.maxWindowSize;
        } else {
            startIdx = this.objectIndexInGeneralizedForm + 1;
        }

        if (startIdx > endIdx)
            words = new ArrayList<>();
        else
            words = allQueryWordsInGeneralizedForm.subList(startIdx, endIdx);
        return words;
    }

    public List<String> getSubjectFollowingWords() {
        List<String> words = new ArrayList<>();

        int startIdx = this.subjectIndexInGeneralizedForm + 1;
        int endIdx;

        if (this.subjectIndexInGeneralizedForm < this.objectIndexInGeneralizedForm) {
            endIdx = this.objectIndexInGeneralizedForm;
        } else {
            endIdx = (this.subjectIndexInGeneralizedForm + Configuration.maxWindowSize >= allQueryWordsInGeneralizedForm.size()) ? allQueryWordsInGeneralizedForm.size() - 1 : this.subjectIndexInGeneralizedForm + Configuration.maxWindowSize;
        }

        if (startIdx > endIdx)
            words = new ArrayList<>();
        else
            words = allQueryWordsInGeneralizedForm.subList(startIdx, endIdx);

        return words;
    }

    public List<String> getObjectPrecedingWords() {
        List<String> words = new ArrayList<>();

        int startIdx;
        int endIdx = this.objectIndexInGeneralizedForm;


        if (this.objectIndexInGeneralizedForm < this.subjectIndexInGeneralizedForm) {
            startIdx = (this.objectIndexInGeneralizedForm - Configuration.maxWindowSize < 0) ? 0 : this.objectIndexInGeneralizedForm - Configuration.maxWindowSize;
        } else {
            startIdx = this.subjectIndexInGeneralizedForm + 1;
        }

        if (startIdx > endIdx)
            words = new ArrayList<>();
        else
            words = allQueryWordsInGeneralizedForm.subList(startIdx, endIdx);

        return words;
    }

    public List<String> getObjectFollowingWords() {
        List<String> words = new ArrayList<>();

        int startIdx = this.objectIndexInGeneralizedForm + 1;
        int endIdx;

        if (this.objectIndexInGeneralizedForm < this.subjectIndexInGeneralizedForm) {
            endIdx = this.subjectIndexInGeneralizedForm;
        } else {
            endIdx = (this.objectIndexInGeneralizedForm + Configuration.maxWindowSize >= allQueryWordsInGeneralizedForm.size()) ? allQueryWordsInGeneralizedForm.size() - 1 : this.objectIndexInGeneralizedForm + Configuration.maxWindowSize;
        }

        if (startIdx > endIdx)
            words = new ArrayList<>();
        else
            words = allQueryWordsInGeneralizedForm.subList(startIdx, endIdx);


        return words;
    }

    public String getObjectHead() {
        return objectHead;
    }

    public String getSubjectHead() {
        return subjectHead;
    }

    public void setSubjectHead(String subjectHead) {
        this.subjectHead = subjectHead;
    }

    public void setObjectHead(String objectHead) {
        this.objectHead = objectHead;
    }

    /*public String getObjectHead() {
        String object = this.getObject();
        String depTreeHash = DepTree.getDepTreeHash(this.getOriginalSentence().getRaw());
        DepTree depTree = new DepTree(depTreeHash, this.getOriginalSentence().getRaw());
        String objectHead = depTree.getHead(object);
        return objectHead;
    }*/
}
