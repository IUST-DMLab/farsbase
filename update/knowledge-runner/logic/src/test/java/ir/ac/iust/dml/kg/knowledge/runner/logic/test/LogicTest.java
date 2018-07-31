package ir.ac.iust.dml.kg.knowledge.runner.logic.test;

import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IDefinitionDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.CommandLine;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Definition;
import ir.ac.iust.dml.kg.knowledge.runner.logic.Manager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Unit test for access
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:logic-context.xml")
public class LogicTest {
    @Autowired
    Manager manager;
    @Autowired
    IDefinitionDao definitions;

    @Test
    public void testProcess() throws InterruptedException, IOException {
        final Definition def = new Definition("logictest", new ArrayList<>());
        def.setMaxTryCount(1);
        final CommandLine step = new CommandLine("java", "Sample");
        step.setWorkingDirectory(Paths.get(getClass().getClassLoader().getResource(".").getFile().substring(1))
                .getParent().getParent().getParent().resolve("sample/target/classes").toString());
        def.getCommands().add(step);
        definitions.write(def);
        manager.run("logictest");
        manager.run("logictest");
        manager.run("logictest");
        Thread.sleep(3000);
        manager.shutdown();

        definitions.delete(def);
    }
}
