package ir.ac.iust.dml.kg.knowledge.runner.services.v1.impl;

import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IDefinitionDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Definition;
import ir.ac.iust.dml.kg.knowledge.runner.services.v1.IDefinitionServices;
import ir.ac.iust.dml.kg.knowledge.runner.services.v1.data.DefinitionData;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.runner.services.v1.IDefinitionServices")
public class DefinitionServicesImpl implements IDefinitionServices {
    @Autowired
    private IDefinitionDao dao;

    @Override
    public List<DefinitionData> all() {
        final List<DefinitionData> result = new ArrayList<>();
        dao.readAll().forEach(d->result.add(new DefinitionData().sync(d)));
        return result;
    }

    @Override
    public DefinitionData insert(DefinitionData data) {
        final Definition old = dao.readByTitle(data.getTitle());
        if(old != null && (data.getIdentifier() == null || !data.getIdentifier().equals(old.getIdentifier())))
            throw new RuntimeException("Title must be unique");
        final Definition newd = data.fill(old);
        dao.write(newd);
        return data.sync(newd);
    }
}
