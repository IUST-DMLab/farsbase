/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.services.web.rest.service;

import ir.ac.iust.dml.kg.evaluation.services.web.rest.model.SimpleSearchResult;
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
public class SearchRestServiceTest {
    
    public SearchRestServiceTest() {
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
     * Test of search method, of class SearchRestService.
     */
    @org.junit.Test
    public void testSearch() {
        System.out.println("search");
        String q = "همسر رامبد جوان";
        SearchRestService instance = new SearchRestService();
        instance.setSearcherUrl("http://dmls.iust.ac.ir:8093/rest/v1/searcher/search");
        List<SimpleSearchResult> expResult = null;
        List<SimpleSearchResult> result = instance.search(q);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
