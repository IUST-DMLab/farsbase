package ir.ac.iust.nlp.jhazm.Test;

import ir.ac.iust.nlp.jhazm.Lemmatizer;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Mojtaba Khallash
 */
public class LemmatizerTest {
    
    @Test
    public void lemmatizeTest() throws IOException {
        Lemmatizer lemmatizer = new Lemmatizer();

        String input, expected, actual;

        input = "کتاب‌ها";
        expected = "کتاب";
        actual = lemmatizer.lemmatize(input);
        assertEquals("Failed to lematize of '" + input + "' word", expected, actual);

        input = "آتشفشان";
        expected = "آتشفشان";
        actual = lemmatizer.lemmatize(input);
        assertEquals("Failed to lematize of '" + input + "' word", expected, actual);

        input = "می‌روم";
        expected = "رفت#رو";
        actual = lemmatizer.lemmatize(input);
        assertEquals("Failed to lematize of '" + input + "' word", expected, actual);

        input = "گفته شده است";
        expected = "گفت#گو";
        actual = lemmatizer.lemmatize(input);
        assertEquals("Failed to lematize of '" + input + "' word", expected, actual);

        input = "مردم";
        expected = "مردم";
        actual = lemmatizer.lemmatize(input, "N");
        assertEquals("Failed to lematize of '" + input + "' word", expected, actual);
    }

    @Test
    public void conjugationsTest() throws IOException {
        Lemmatizer lemmatizer = new Lemmatizer();

        String input;
        String[] expected;
        List<String> actual;

        input = "خورد#خور";
        expected = new String[] { 
            "خوردم", "خوردی", "خورد", "خوردیم", "خوردید", "خوردند", 
            "نخوردم", "نخوردی", "نخورد", "نخوردیم", "نخوردید", "نخوردند", 
            "خورم", "خوری", /*"خورد",*/ "خوریم", "خورید", "خورند", 
            "نخورم", "نخوری", /*"نخورد",*/ "نخوریم", "نخورید", "نخورند", 
            "می‌خوردم", "می‌خوردی", /*"می‌خورد",*/ "می‌خوردیم", "می‌خوردید", "می‌خوردند", 
            "نمی‌خوردم", "نمی‌خوردی", "نمی‌خورد", "نمی‌خوردیم", "نمی‌خوردید", "نمی‌خوردند", 
            "خورده‌ام", "خورده‌ای", "خورده", "خورده‌ایم", "خورده‌اید", "خورده‌اند", 
            "نخورده‌ام", "نخورده‌ای", "نخورده", "نخورده‌ایم", "نخورده‌اید", "نخورده‌اند", 
            "می‌خورم", "می‌خوری", "می‌خورد", "می‌خوریم", "می‌خورید", "می‌خورند", 
            "نمی‌خورم", "نمی‌خوری", /*"نمی‌خورد",*/ "نمی‌خوریم", "نمی‌خورید", "نمی‌خورند", 
            "بخورم", "بخوری", "بخورد", "بخوریم", "بخورید", "بخورند", 
            "بخور", "نخور" 
        };
        actual = lemmatizer.conjugations(input);
        check(input, expected, actual);

        input = "آورد#آور";
        expected = new String[] { 
            "آوردم", "آوردی", "آورد", "آوردیم", "آوردید", "آوردند", 
            "نیاوردم", "نیاوردی", "نیاورد", "نیاوردیم", "نیاوردید", "نیاوردند", 
            "آورم", "آوری", /*"آورد",*/ "آوریم", "آورید", "آورند", 
            "نیاورم", "نیاوری", /*"نیاورد",*/ "نیاوریم", "نیاورید", "نیاورند", 
            "می‌آوردم", "می‌آوردی", /*"می‌آورد",*/ "می‌آوردیم", "می‌آوردید", "می‌آوردند", 
            "نمی‌آوردم", "نمی‌آوردی", "نمی‌آورد", "نمی‌آوردیم", "نمی‌آوردید", "نمی‌آوردند", 
            "آورده‌ام", "آورده‌ای", "آورده", "آورده‌ایم", "آورده‌اید", "آورده‌اند", 
            "نیاورده‌ام", "نیاورده‌ای", "نیاورده", "نیاورده‌ایم", "نیاورده‌اید", "نیاورده‌اند", 
            "می‌آورم", "می‌آوری", "می‌آورد", "می‌آوریم", "می‌آورید", "می‌آورند", 
            "نمی‌آورم", "نمی‌آوری", /*"نمی‌آورد",*/ "نمی‌آوریم", "نمی‌آورید", "نمی‌آورند", 
            "بیاورم", "بیاوری", "بیاورد", "بیاوریم", "بیاورید", "بیاورند", 
            "بیاور", "نیاور"
        };
        actual = lemmatizer.conjugations(input);
        check(input, expected, actual);
    }
        
    private void check(String input, String[] expected, List<String> actual) {
        assertEquals("Failed to generate conjugations of '" + input + "' verb", expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            if (!actual.contains(expected[i]))
                assertEquals("Failed to generate conjugations of '" + input + "' verb", expected[i], actual.get(i));
        }
    }
}