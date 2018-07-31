package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.distantsupervison.models.Classifier;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

import static com.google.common.io.Files.isDirectory;

/**
 * Created by hemmatan on 9/19/2017.
 */
public class ModelHandler {
    public static String decide(CorpusEntryObject corpusEntryObject){
        String[] names = Configuration.classifierTypes;
        for (String name :
                names){
            File curFile = new File(SharedResources.logitDirectory + name);
            if (curFile.isDirectory()){
                try (Scanner scanner = new Scanner(new FileInputStream(curFile + File.separator + "allowedEntityTypes.txt"))) {
                    String subjectType = scanner.nextLine().replace("\uFEFF", "");
                    String objectType = scanner.nextLine();
                    if (corpusEntryObject.getSubjectType().contains(subjectType) &&
                            corpusEntryObject.getObjectType().contains(objectType))
                        return name;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return Constants.classifierTypes.GENERAL;
    }

    public static void trainAllModels(){
        String[] names = Configuration.classifierTypes;
        Date date = new Date();
        String dateString = date.toString().replaceAll("[: ]", "-");

        for (String name :
                names){
            PrintStream out = null;

            File curFile = new File(SharedResources.logitDirectory + name);
            if (curFile.isDirectory()){

                Classifier classifier = new Classifier(name);
                try {
                    out = new PrintStream(new FileOutputStream(
                            classifier.fullPath("consoleOutput-" + dateString + ".txt")));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                System.setOut(out);

                classifier.train((int) Configuration.maximumNumberOfTrainExamples, true);
            }
        }
    }
}
