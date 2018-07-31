package ir.ac.iust.dml.kg.raw.distantsupervison.models;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created by hemmatan on 5/8/2017.
 */
public class PartOfSpeechModel {
    private String posFile;
    private int noOfPOS = 0;
    private Set<String> partsOfSpeech = new HashSet<>();
    private HashMap<String, Integer> posIndex = new HashMap<>();
    private HashMap<Integer, String> posInvertedIndex = new HashMap<>();

    public PartOfSpeechModel(String posFile) {
        this.posFile = posFile;
    }

    public void addToModel(List<String> posTagged) {
        int lastIdx = noOfPOS;
        for (String pos :
                posTagged) {
            if (!posIndex.containsKey((pos))) {
                posIndex.put(pos, lastIdx);
                posInvertedIndex.put(lastIdx, pos);
                lastIdx++;
            }
        }
        noOfPOS = posIndex.keySet().size();
        partsOfSpeech = posIndex.keySet();
    }

    public void saveModel() {
        try (Writer fileWriter = new FileWriter(this.posFile)) {
            Set<String> pos = this.posIndex.keySet();
            for (String s :
                    pos) {
                fileWriter.write(s + "\t" + posIndex.get(s) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadModel() {
        try (Scanner scanner = new Scanner(new FileInputStream(this.posFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replace("\uFEFF", "");
                String pos = line.split("\t")[0];
                int lastIdx = Integer.parseInt(line.split("\t")[1]);
                posIndex.put(pos, lastIdx);
                posInvertedIndex.put(lastIdx, pos);
            }
            noOfPOS = posIndex.keySet().size();
            partsOfSpeech = posIndex.keySet();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNoOfPOS() {
        return noOfPOS;
    }

    public void setNoOfPOS(int noOfPOS) {
        this.noOfPOS = noOfPOS;
    }

    public Set<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(Set<String> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public HashMap<String, Integer> getPosIndex() {
        return posIndex;
    }

    public void setPosIndex(HashMap<String, Integer> posIndex) {
        this.posIndex = posIndex;
    }

    public HashMap<Integer, String> getPosInvertedIndex() {
        return posInvertedIndex;
    }

    public void setPosInvertedIndex(HashMap<Integer, String> posInvertedIndex) {
        this.posInvertedIndex = posInvertedIndex;
    }
}
