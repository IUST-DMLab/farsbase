package ir.ac.iust.dml.kg.raw.distantsupervison.database;

import com.mongodb.*;
import ir.ac.iust.dml.kg.raw.distantsupervison.Configuration;
import ir.ac.iust.dml.kg.raw.distantsupervison.Constants;
import ir.ac.iust.dml.kg.raw.distantsupervison.Sentence;

import java.util.List;

import static ir.ac.iust.dml.kg.raw.distantsupervison.SharedResources.corpus;

/**
 * Created by hemmatan on 4/9/2017.
 */
public class SentenceDbHandler extends DbHandler {


    public void loadSentenceTable() {
        //if (corpus.getSentences().isEmpty())
        //  createCorpusTableFromWikiDump();

        corpus.clear();

        MongoClient mongo = null;

            mongo = new MongoClient(host, port);
        DB distantSupervisionDB = mongo.getDB(Configuration.distantSupervisionDBName);

        DBCollection corpusTable = distantSupervisionDB.getCollection(Configuration.sentencesTableName);
            DBCursor cursor = corpusTable.find();

            int cnt = 0;

            while (cursor.hasNext()){
                cnt+=1;
                DBObject dbSentence = cursor.next();
                String rawString = (String) dbSentence.get(Constants.sentenceAttribs.RAW);
                BasicDBList wordsObject = (BasicDBList) dbSentence.get(Constants.sentenceAttribs.WORDS);
                BasicDBList postagObject = (BasicDBList) dbSentence.get(Constants.sentenceAttribs.POSTAG);
                List<String> words = convertBasicDBListToJavaListOfStrings(wordsObject);
                List<String> posTags = convertBasicDBListToJavaListOfStrings(postagObject);
                Sentence currentSentence = new Sentence(rawString,words,posTags);
                corpus.addSentence(currentSentence);
            }


    }
}
