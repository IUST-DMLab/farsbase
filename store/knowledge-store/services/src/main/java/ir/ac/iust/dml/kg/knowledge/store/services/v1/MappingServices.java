package ir.ac.iust.dml.kg.knowledge.store.services.v1;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access.dao.IMappingDao;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TemplateMapping;
import ir.ac.iust.dml.kg.knowledge.store.services.v1.data.TemplateData;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.validation.Valid;
import java.util.List;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link IMappingServices}
 */
@Deprecated
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v1.IMappingServices")
public class MappingServices implements IMappingServices {
    @Autowired
    private IMappingDao dao;


    @Override
    public Boolean insert(@Valid TemplateData data) {
        final TemplateMapping old = dao.read(data.getTemplate());
        final TemplateMapping mapping = data.fill(old);
        dao.write(mapping);
        return true;
    }

    @Override
    public Boolean batchInsert(@Valid List<TemplateData> data) {
        data.forEach(this::insert);
        return true;
    }

    @Override
    public PagingList<TemplateMapping> readAll(int page, int pageSize) {
        return dao.readTemplate(true, true, page, pageSize);
    }
}
