package ir.ac.iust.dml.kg.raw.distantsupervison;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ir.ac.iust.dml.kg.raw.SentenceTokenizer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ir.ac.iust.dml.kg.raw.distantsupervison.SharedResources.*;

/**
 * Created by hemmatan on 4/5/2017.
 */
public class RawTextHandler {
    public static void loadRawText(){
        Scanner reader;
        try {
            int numberOfLines = 0;
            reader = new Scanner(new FileInputStream(rawTextPath.toString()));
            //TODO: move numberOfLines to Configuartion
            while (reader.hasNextLine() && numberOfLines<=50000){
                String line = reader.nextLine().replace("\uFEFF", "");
                rawText += line;
                rawTextLines.add(line);
                numberOfLines++;
                System.out.println(numberOfLines);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void buildCorpus(){
        corpus = new Corpus();
        List<Sentence> sentenceList = new ArrayList<Sentence>();
        for (String line:
             rawTextLines) {
            List<String> lineSentences = SentenceTokenizer.SentenceSplitterRaw(line);
            for (String sentenceString:
                 lineSentences) {
                Sentence sentence = new Sentence(sentenceString);
                corpus.addSentence(sentence);
            }
        }
    }

    public static void saveCorpus(){
        JsonWriter writer;
        try {
            System.out.print(corpusPath.toString());
            writer = new JsonWriter(new FileWriter(corpusPath.toString()));
            List<Sentence> sentenceList = corpus.getSentences();
            writer.beginArray();
            for (Sentence sentence:
                 sentenceList) {
                writer.beginObject();
                writer.name(Constants.sentenceAttribs.RAW).value(sentence.getRaw());
                writer.name(Constants.sentenceAttribs.WORDS).value(sentence.getWords().toString());
                writer.name(Constants.sentenceAttribs.POSTAG).value(sentence.getPosTagged().toString());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> stringToList(String string){
        List<String> tokens = new ArrayList<String>();
        string = string.substring(1, string.length()-1);
        tokens = Arrays.asList(string.split(","));
        return tokens;
    }

    public static void loadCorpus() {
        try {
            JsonReader reader = new JsonReader(new FileReader(corpusPath.toString()));
            reader.beginArray();
            corpus = new Corpus();
            while(reader.hasNext()) {
                JsonToken nextToken = reader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)){
                    reader.beginObject();
                    Sentence sentence = new Sentence();
                    String name = reader.nextName();
                    String value_raw = reader.nextString();
                    sentence.setRaw(value_raw);
                    name = reader.nextName();
                    String value_words = reader.nextString();
                    sentence.setWords(stringToList(value_words));
                    name = reader.nextName();
                    String value_postag = reader.nextString();
                    sentence.setPosTagged(stringToList(value_postag));
                    corpus.addSentence(sentence);
                    reader.endObject();
                }
            }
            //reader.endArray();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
