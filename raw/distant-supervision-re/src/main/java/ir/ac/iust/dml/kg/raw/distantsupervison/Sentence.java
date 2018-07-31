package ir.ac.iust.dml.kg.raw.distantsupervison;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.WordTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemmatan on 4/5/2017.
 */
public class Sentence {
    private String raw;
    private String normalized;
    private List<String> words;
    private List<String> posTagged;

    public Sentence() {
        raw = "";
        words = new ArrayList<String>();
        posTagged = new ArrayList<String>();

    }

    public Sentence(String raw) {
        this.raw = raw;
        this.normalized = Normalizer.normalize(raw);
        this.words = WordTokenizer.tokenize(normalized);
        this.posTagged = new ArrayList<String>();
        List<TaggedWord> taggedWords = POSTagger.tag(this.words);

        for (TaggedWord taggedWord :
                taggedWords) {
            this.posTagged.add(taggedWord.tag());
        }

        if (this.words.size() != this.posTagged.size()) {
            System.out.println("error in sentence ");
            System.out.println(words);
            System.out.println(posTagged);
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Sentence(String raw, List<String> words, List<String> posTagged) {
        this.raw = raw;
        this.words = words;
        this.posTagged = posTagged;
    }

    public Sentence(String raw, List<String> words, List<String> posTagged, String normalized) {
        this.raw = raw;
        this.words = words;
        this.posTagged = posTagged;
        this.normalized = normalized;
    }

    public String getNormalized() {
        return normalized;
    }

    public void setNormalized(String normalized) {
        this.normalized = normalized;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<String> getPosTagged() {
        return posTagged;
    }

    public void setPosTagged(List<String> posTagged) {
        this.posTagged = posTagged;
    }
}
