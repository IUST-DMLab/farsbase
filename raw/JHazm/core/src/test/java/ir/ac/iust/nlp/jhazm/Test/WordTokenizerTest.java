package ir.ac.iust.nlp.jhazm.Test;

import ir.ac.iust.nlp.jhazm.WordTokenizer;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Mojtaba Khallash
 */
public class WordTokenizerTest {
    
    @Test
    public void tokenizeTest() throws IOException {
        WordTokenizer wordTokenizer = new WordTokenizer(false);

        String input;
        String[] expected;
        List<String> actual;

        input = "این جمله (خیلی) پیچیده نیست!!!";
        expected = new String[] { "این", "جمله", "(", "خیلی", ")", "پیچیده", "نیست", "!!!"};
        actual = wordTokenizer.tokenize(input);
        check(input, expected, actual);
    }

    @Test
    public void joinVerbPartsTest() throws IOException {
        WordTokenizer wordTokenizer = new WordTokenizer(true);

        String input;
        String[] expected;
        List<String> actual;

        input = "خواهد رفت";
        expected = new String[] { "خواهد رفت" };
        actual = wordTokenizer.tokenize(input);
        check(input, expected, actual);

        input = "رفته است";
        expected = new String[] { "رفته است" };
        actual = wordTokenizer.tokenize(input);
        check(input, expected, actual);

        input = "گفته شده است";
        expected = new String[] { "گفته شده است" };
        actual = wordTokenizer.tokenize(input);
        check(input, expected, actual);

        input = "گفته خواهد شد";
        expected = new String[] { "گفته خواهد شد" };
        actual = wordTokenizer.tokenize(input);
        check(input, expected, actual);

        input = "خسته شدید";
        expected = new String[] { "خسته", "شدید" };
        actual = wordTokenizer.tokenize(input);
        check(input, expected, actual);
    }
    
    private void check(String input, String[] expected, List<String> actual) {
        assertEquals("Failed to tokenize words of '" + input + "' sentence", expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Failed to tokenize words of '" + input + "' sentence", expected[i], actual.get(i));
        }
    }
}