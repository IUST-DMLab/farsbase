package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.distantsupervison.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static ir.ac.iust.dml.kg.raw.distantsupervison.database.DbHandler.saveCorpusJasonToDB;

/**
 * Created by hemmatan on 6/10/2017.
 */
public class JSONHandler {

    public static JSONArray getJsonArrayFromURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // read from the URL
        Scanner scan = null;
        try {
            scan = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = new String();
        while (scan.hasNext())
            str += scan.nextLine().replace("\uFEFF", "");
        scan.close();

        // build a JSON array
        JSONArray arr = new JSONArray(str);

        return arr;
    }

    public static void appendJsonObjectToFile(JSONObject jsonObject, FileWriter fileWriter) {
        try {
            fileWriter.append(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sample() throws Exception {
        // build a URL
        String s = Configuration.exportURL;
        URL url = new URL(s);

        // read from the URL
        Scanner scan = new Scanner(url.openStream());
        String str = new String();
        while (scan.hasNext())
            str += scan.nextLine().replace("\uFEFF", "");
        scan.close();

        // build a JSON object
        JSONArray arr = new JSONArray(str);

        JSONObject temp = arr.getJSONObject(0);
        System.out.println(temp.get("predicate"));

    }

    public static void makeDBsFromJson(){
        saveCorpusJasonToDB(Configuration.corpusTableName);
        saveCorpusJasonToDB(Configuration.negativesTableName);
    }
}
