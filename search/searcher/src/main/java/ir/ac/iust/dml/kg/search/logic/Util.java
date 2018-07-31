package ir.ac.iust.dml.kg.search.logic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by ali on 4/16/17.
 */
public class Util {
    public static boolean textIsPersian(String s) {
        for (int i = 0; i < Character.codePointCount(s, 0, s.length()); i++) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06FF || c == 0xFB8A || c == 0x067E || c == 0x0686 || c == 0x06AF)
                return true;
        }
        return false;
    }


    /**
     * Used for removing duplicates by property. see https://stackoverflow.com/q/23699371/2571490
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static String cleanText(String uri){
        return uri.replace("@fa","").replaceAll("@en", "");
    }


    public static String iriToLabel(String iri)
    {
        iri = iri.replace("@fa","").replace("fa@","");
        String[] splits = iri.split("/");
        return splits[splits.length-1].replace('_',' ');
    }

    // https://stackoverflow.com/questions/1660501/what-is-a-good-64bit-hash-function-in-java-for-textual-strings
    public static long longHash(String string) {
        long h = 1125899906842597L; // prime
        int len = string.length();

        for (int i = 0; i < len; i++) {
            h = 31*h + string.charAt(i);
        }
        return h;
    }

    // https://stackoverflow.com/questions/1660501/what-is-a-good-64bit-hash-function-in-java-for-textual-strings
    public static long longHash(String string1, String string2) {
        return longHash(string1) * 31 + longHash(string2);
    }





    public static void main(String[] args) {
        String s = "۱۸ فروردین ۱۳۴۶ ()@fa";
        System.out.println(s.replaceAll("@fa", ""));
    }


}
