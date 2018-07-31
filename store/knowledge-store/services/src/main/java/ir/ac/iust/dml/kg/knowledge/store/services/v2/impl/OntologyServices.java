package ir.ac.iust.dml.kg.knowledge.store.services.v2.impl;


import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IOntologyDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Ontology;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.IOntologyServices;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.OntologyData;
import ir.ac.iust.dml.kg.raw.utils.URIs;
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
 * impl {@link IOntologyServices}
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v2.IOntologyServices")
public class OntologyServices implements IOntologyServices {
    @Autowired
    private IOntologyDao dao;

    @Override
    public OntologyData insert(@Valid OntologyData data) {
        if (data.getContext() == null) data.setContext(URIs.INSTANCE.getDefaultContext());
        final Ontology old = dao.read(data.getContext(), data.getSubject(), data.getPredicate(), data.getObject().getValue());
        final Ontology ontology = data.fill(old);
        dao.write(ontology);
        return new OntologyData().sync(ontology);
    }

    @Override
    public List<OntologyData> batchInsert(@Valid List<OntologyData> data) {
        final List<OntologyData> ontology = new ArrayList<>();
        data.forEach(i-> ontology.add(insert(i)));
        return ontology;
    }

    @Override
    public Ontology remove(String context, String subject, String predicate, String object) {
        if (context == null) context = URIs.INSTANCE.getDefaultContext();
        Ontology ontology = dao.read(context, subject, predicate, object);
        if (ontology != null)
            dao.delete(ontology);
        return ontology;
    }

    @Override
    public Ontology ontology(String context, String subject, String predicate, String object) {
        if (context == null) context = URIs.INSTANCE.getDefaultContext();
        return dao.read(context, subject, predicate, object);
    }

    @Override
    public PagingList<Ontology> search(String context, Boolean contextLike,
                                       String subject, Boolean subjectLike,
                                       String predicate, Boolean predicateLike,
                                       String object, Boolean objectLike,
                                       boolean approved, int page, int pageSize) {
        return dao.search(context, contextLike, subject, subjectLike, predicate, predicateLike,
                object, objectLike, approved ? TripleState.Approved : null, page, pageSize);
    }
}
