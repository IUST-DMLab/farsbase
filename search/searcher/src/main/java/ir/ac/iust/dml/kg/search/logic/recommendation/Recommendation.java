package ir.ac.iust.dml.kg.search.logic.recommendation;

import ir.ac.iust.dml.kg.search.logic.KGFetcher;

public class Recommendation {
    private double score;
    private String description;
    private String uri;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void deduplicate() {
        description = KGFetcher.intern(description);
        uri = KGFetcher.intern((uri));
    }
}