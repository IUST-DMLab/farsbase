package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.DependencyParser;
import ir.ac.iust.dml.kg.raw.WordTokenizer;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemmatan on 8/2/2017.
 */
public class DepTree {
    private final List<String> words;
    private List<DepTreeNone> nodes = new ArrayList<>();
    private String raw = "";



    public DepTree(String depTreeHash, String raw){
        String temp = depTreeHash.replace("][", " ");
        temp = temp.replace("[" , "");
        temp = temp.replace("]" , "");
        String[] tokens = temp.split(" ");
        for (String token:
             tokens) {
            String[] tmp = token.split(",");
            nodes.add(new DepTreeNone(tmp[0], Integer.valueOf(tmp[1]), tmp[2]));
        }
        this.raw = raw;
        this.words = WordTokenizer.tokenize(raw);

    }

    public String getEntitysVerbHead(String sentence, String entity) {
        ConcurrentDependencyGraph result = DependencyParser.parseRaw(sentence).get(0);
        int idx = -1;
        for (int i = 1; i < result.nTokenNodes() + 1; i++) {
            ConcurrentDependencyNode node = result.getDependencyNode(i);
            if (node.getLabel("FORM").equalsIgnoreCase(entity))
                idx = i;
        }
        if (idx == -1)
            return "null";

        ConcurrentDependencyNode node = result.getDependencyNode(idx);
        while (true) {
            ConcurrentDependencyNode parent = node.getHead();
            if (parent.getLabel("POSTAG").equalsIgnoreCase("V"))
                return parent.getLabel("FORM");
            node = node.getHead();
        }
    }

    public String getNearestFollowingVerb(String entity){
        String entity1 = WordTokenizer.tokenize(entity).get(0);
        for(int i = words.indexOf(entity1); i<words.size(); i++){
            if (this.nodes.get(i).getPos().equalsIgnoreCase("V"))
                if (i!=0)
                    return this.words.get(i-1)+" "+this.words.get(i);
        }
        return "";
    }

    public String getNearestPrecedingVerb(String entity){
        String entity1 = WordTokenizer.tokenize(entity).get(0);
        for(int i = words.indexOf(entity1)-1; i>0; i--){
            if (this.nodes.get(i).getPos().equalsIgnoreCase("V"))
                if (i!=0)
                    return this.words.get(i-1)+" "+this.words.get(i);        }
        return "";
    }

    public String getHead(String entity){
        String entity1 = WordTokenizer.tokenize(entity).get(0);
        int i = words.indexOf(entity1);
        int idx = nodes.get(i).getHead()-1;
        if (idx == -1)
            return "null";
        return words.get(idx);
    }

    public static String getDepTreeHash(String sentence) {
        if (sentence.isEmpty()) return "";
        String depTreeHash = "";
        ConcurrentDependencyGraph result = DependencyParser.parseRaw(sentence).get(0);
        for (int i = 1; i < result.nTokenNodes() + 1; i++) {
            ConcurrentDependencyNode node = result.getDependencyNode(i);
            String postag = node.getLabel("POSTAG");
            String headid = "0";
            if (!node.getHead().getLabel("ID").equalsIgnoreCase(""))
                headid = node.getHead().getLabel("ID");
            String deprel = node.getLabel("DEPREL");
            depTreeHash += "[" + postag + "," + headid + "," + deprel + "]";
        }
        return depTreeHash;
    }
}
