package ir.ac.iust.dml.kg.knowledge.store.access.test;

import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IDefinitionDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IHistoryDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IRunDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Definition;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunHistory;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

/**
 * Unit test for access
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:persistence-context.xml")
public class AccessTest {
    @Autowired
    IHistoryDao historyDao;
    @Autowired
    IRunDao runDao;
    @Autowired
    IDefinitionDao definitionDao;

    @Test
    public void run() {
        Run run = new Run("title", new ArrayList<>());
        runDao.write(run);
        assert runDao.readAllNeedForRerun().size() == 1;
        run.setState(RunState.Succeed);
        runDao.write(run);
        assert runDao.readAllNeedForRerun().size() == 0;
        runDao.delete(run);
    }

    @Test
    public void history() throws Exception {
        final Run run = new Run("title", new ArrayList<>());
        runDao.write(run);
        try (RunHistory hist1 = historyDao.create(run)) {
            try (RunHistory hist2 = historyDao.create(run)) {
                assert false;
            } catch (Exception ignored) {

            }
            hist1.appendError("A");
            hist1.appendError("B");
            hist1.appendOutput("C");
        }

        try (RunHistory read = historyDao.read(run)) {
            assert read.getErrorLines().size() == 2;
            assert read.getOutputLines().size() == 1;
        }
        runDao.delete(run);
    }

    @Test
    public void definition() {
        final Definition definition = new Definition("title", new ArrayList<>());
        definitionDao.write(definition);
        try {
            definitionDao.write(new Definition("title", new ArrayList<>()));
            assert false;
        } catch (Throwable ignored) {

        }
        assert definitionDao.readAll().size() == 1;
        assert definitionDao.readByTitle("title") != null;
        definitionDao.delete(definition);
        assert definitionDao.readAll().isEmpty();
    }
}
