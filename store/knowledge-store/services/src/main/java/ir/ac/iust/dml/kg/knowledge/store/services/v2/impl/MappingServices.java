package ir.ac.iust.dml.kg.knowledge.store.services.v2.impl;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IMappingDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TemplateMapping;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.IMappingServices;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.TemplateData;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link IMappingServices}
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v2.IMappingServices")
public class MappingServices implements IMappingServices {
    @Autowired
    private IMappingDao dao;


    @Override
    public TemplateData insert(@Valid TemplateData data) {
        final TemplateMapping old = dao.read(data.getTemplate());
        final TemplateMapping mapping = data.fill(old);
        dao.write(mapping);
        return new TemplateData().sync(mapping);
    }

    @Override
    public List<TemplateData> batchInsert(@Valid List<TemplateData> data) {
        List<TemplateData> result = new ArrayList<>();
        data.forEach(i -> result.add(insert(i)));
        return result;
    }

    @Override
    public PagingList<TemplateMapping> readAll(int page, int pageSize) {
        return dao.readTemplate(true, true, page, pageSize);
    }
}
