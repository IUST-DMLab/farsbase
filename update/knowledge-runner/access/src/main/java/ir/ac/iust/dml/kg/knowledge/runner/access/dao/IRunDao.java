package ir.ac.iust.dml.kg.knowledge.runner.access.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
public interface IRunDao {
    void write(Run... runs);

    void delete(Run... runs);

    Run read(ObjectId id);

    List<Run> readAllNeedForRerun();

    PagingList<Run> search(String title, RunState state, int page, int pageSize);
}
