package ir.ac.iust.dml.kg.knowledge.expert.access.test;

import ir.ac.iust.dml.kg.knowledge.store.client.Triple;
import ir.ac.iust.dml.kg.knowledge.store.client.V1StoreClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Unit test for client
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:persistence-context.xml")
public class ClientTest {
    @Autowired
    V1StoreClient client;

    @Test
    public void test() {
//        List<Triple> triples = client.triples("test", 10);

        List<Triple> triples2 = client.triplesSubject("wiki", "hossein", null, null, 10000);
    }
}
