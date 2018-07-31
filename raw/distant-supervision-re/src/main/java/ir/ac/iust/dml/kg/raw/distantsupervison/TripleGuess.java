package ir.ac.iust.dml.kg.raw.distantsupervison;

/**
 * Created by hemmatan on 8/9/2017.
 */
public class TripleGuess {
    private String subject;
    private String object;
    private String predicate;
    private double confidence;
    private String originSentence;
    private String source;

    public TripleGuess(String subject, String object, String predicate, double confidence, String originSentence) {
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;
        this.confidence = confidence;
        this.originSentence = originSentence;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getOriginSentence() {
        return originSentence;
    }

    public void setOriginSentence(String originSentence) {
        this.originSentence = originSentence;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
