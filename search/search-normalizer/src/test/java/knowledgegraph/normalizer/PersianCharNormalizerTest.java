/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledgegraph.normalizer;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author morteza.khaleghi
 */
public class PersianCharNormalizerTest {
    
    public PersianCharNormalizerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of normalize method, of class PersianCharNormalizer.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        String text = "تست۴٤";
        List<PersianCharNormalizer.Option> options=new ArrayList<>();
        
        PersianCharNormalizer instance = new PersianCharNormalizer();
        
        String expResult = "تست44";
        String result = instance.normalize(text);
        assertEquals(expResult, result);
        
    }
    
}
