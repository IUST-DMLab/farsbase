package ir.ac.iust.dml.kg.raw.distantsupervison.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import ir.ac.iust.dml.kg.raw.distantsupervison.Configuration;
import ir.ac.iust.dml.kg.raw.distantsupervison.Constants;
import ir.ac.iust.dml.kg.raw.distantsupervison.TripleGuess;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import org.bson.Document;

/**
 * Created by hemmatan on 8/12/2017.
 */
public class ExtractedTriplesDBHandler extends DbHandler {
    private MongoClient mongo = null;
    private MongoDatabase extractedTriplesDB;
    private MongoCollection<Document> triplesTable;

    public ExtractedTriplesDBHandler(String tableName) {
        mongo = new MongoClient(host, port);
        extractedTriplesDB = mongo.getDatabase(Configuration.distantSupervisionDBName);
        triplesTable = extractedTriplesDB.getCollection(tableName);
    }


    public void insert(TripleGuess tripleGuess) {
        Document document = new Document();
        document.put(Constants.triplesAttribs.SUBJECT, tripleGuess.getSubject());
        document.put(Constants.triplesAttribs.OBJECT, tripleGuess.getObject());
        document.put(Constants.triplesAttribs.PREDICATE, tripleGuess.getPredicate());
        document.put(Constants.triplesAttribs.CONFIDENCE, tripleGuess.getConfidence());
        document.put(Constants.triplesAttribs.SOURCE, tripleGuess.getSource());
        document.put(Constants.triplesAttribs.ORIGIN_SENTENCE, tripleGuess.getOriginSentence());
        triplesTable.insertOne(document);
    }

    public void insert(RawTriple rawTriple) {
        Document document = new Document();
        document.put(Constants.triplesAttribs.SUBJECT, rawTriple.getSubject());
        document.put(Constants.triplesAttribs.OBJECT, rawTriple.getObject());
        document.put(Constants.triplesAttribs.PREDICATE, rawTriple.getPredicate());
        document.put(Constants.triplesAttribs.CONFIDENCE, rawTriple.getAccuracy());
        document.put(Constants.triplesAttribs.SOURCE, rawTriple.getModule());
        document.put(Constants.triplesAttribs.ORIGIN_SENTENCE, rawTriple.getRawText());
        triplesTable.insertOne(document);
    }


}
