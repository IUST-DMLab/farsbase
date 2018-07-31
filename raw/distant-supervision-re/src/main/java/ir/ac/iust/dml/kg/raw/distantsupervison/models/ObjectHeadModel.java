package ir.ac.iust.dml.kg.raw.distantsupervison.models;

import ir.ac.iust.dml.kg.raw.distantsupervison.CorpusEntryObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created by hemmatan on 8/16/2017.
 */
public class ObjectHeadModel {
    private String modelFile;
    private int noOfHeads;

    public int getMaxNoOfHeads() {
        return maxNoOfHeads;
    }

    private int maxNoOfHeads;
    private List<String> heads;
    private Set<String> headSet = new HashSet<>();
    private HashMap<String, Integer> headIndex = new HashMap<>();
    private HashMap<Integer, String> headInvertedIndex = new HashMap<>();

    public ObjectHeadModel(int i, String modelFile) {
        this.maxNoOfHeads = i;
        this.modelFile = modelFile;
    }

    public ObjectHeadModel(List<CorpusEntryObject> entries, int i, String modelFile) {
        HashMap<String, Double> headCount = new HashMap<>();
        maxNoOfHeads = i;
        this.modelFile = modelFile;
        for (CorpusEntryObject corpusEntryObject :
                entries) {
            System.out.println(corpusEntryObject.getOriginalSentence().getRaw());
            String objectHead;
            if (this.modelFile.equalsIgnoreCase("objectHead"))
                objectHead = corpusEntryObject.getObjectHead();
            else
                objectHead = corpusEntryObject.getSubjectHead();
            if (headCount.containsKey(objectHead))
                headCount.put(objectHead, headCount.get(objectHead) + 1);
            else headCount.put(objectHead, 1.0);
        }
        int curNoOfHeads = headCount.size();
        Comparator<Map.Entry> hashmapValueComparator = new HashmapValueComparator();
        PriorityQueue<Map.Entry> priorityQueue = new PriorityQueue<>(this.maxNoOfHeads, hashmapValueComparator);
        Iterator tfInCorpusIterator = headCount.entrySet().iterator();
        while (tfInCorpusIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) tfInCorpusIterator.next();
            priorityQueue.add(pair);
        }
        Integer currentIndex = 0;
        while (!priorityQueue.isEmpty() && currentIndex < this.maxNoOfHeads && currentIndex < curNoOfHeads) {
            Map.Entry pair = priorityQueue.poll();
            addToModel((String) pair.getKey());
            currentIndex++;
        }
        saveModel();
    }

    private void addToModel(String head) {
        int lastIdx = noOfHeads;
        if (!headIndex.containsKey((head))) {
            headIndex.put(head, lastIdx);
            headInvertedIndex.put(lastIdx, head);
        }
        noOfHeads = headIndex.keySet().size();
        headSet = headIndex.keySet();
    }

    public void saveModel() {
        try (Writer fileWriter = new FileWriter(this.modelFile)) {
            Set<String> entities = this.headIndex.keySet();
            for (String s :
                    entities) {
                fileWriter.write(s + "\t" + headIndex.get(s) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadModel() {
        try (Scanner scanner = new Scanner(new FileInputStream(this.modelFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replace("\uFEFF", "");
                String pos = line.split("\t")[0];
                int lastIdx = Integer.parseInt(line.split("\t")[1]);
                headIndex.put(pos, lastIdx);
                headInvertedIndex.put(lastIdx, pos);
            }
            noOfHeads = headIndex.keySet().size();
            //heads = headIndex.keySet();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNoOfHeads() {
        return noOfHeads;
    }

    public void setNoOfHeads(int noOfHeads) {
        this.noOfHeads = noOfHeads;
    }

    public HashMap<String, Integer> getHeadIndex() {
        return headIndex;
    }

    public void setHeadIndex(HashMap<String, Integer> headIndex) {
        this.headIndex = headIndex;
    }

    public HashMap<Integer, String> getHeadInvertedIndex() {
        return headInvertedIndex;
    }

    public void setHeadInvertedIndex(HashMap<Integer, String> headInvertedIndex) {
        this.headInvertedIndex = headInvertedIndex;
    }
}
