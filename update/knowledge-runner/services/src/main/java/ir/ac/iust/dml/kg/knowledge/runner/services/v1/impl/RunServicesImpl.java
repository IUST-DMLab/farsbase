package ir.ac.iust.dml.kg.knowledge.runner.services.v1.impl;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IRunDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;
import ir.ac.iust.dml.kg.knowledge.runner.logic.Manager;
import ir.ac.iust.dml.kg.knowledge.runner.services.v1.IRunServices;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.runner.services.v1.IRunServices")
public class RunServicesImpl implements IRunServices {
    @Autowired
    private Manager manager;
    @Autowired
    private IRunDao dao;


    @Override
    public Run run(String title) {
        return manager.run(title);
    }

    @Override
    public List<Run> allRunning() {
        return manager.getAllRunning();
    }

    @Override
    public PagingList<Run> search(String title, RunState state, int page, int pageSize) {
        return dao.search(title, state, page, pageSize);
    }
}
