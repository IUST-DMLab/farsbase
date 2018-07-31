package ir.ac.iust.dml.kg.knowledge.store.services.v2.impl;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IMappingDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.MapRule;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.PropertyMapping;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TemplateMapping;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.IMappingExpertServices;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.MapRuleData;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.TemplateData;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.validation.Valid;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link IMappingExpertServices}
 */
@SuppressWarnings("Duplicates")
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v2.IMappingExpertServices")
public class MappingExpertServices implements IMappingExpertServices {
    @Autowired
    private IMappingDao db;

    @Override
    public PagingList<PropertyMapping> searchProperty(String template, String property, int page, int pageSize) {
        return db.searchProperty(template, property, page, pageSize);
    }

    @Override
    public PagingList<TemplateMapping> searchTemplate(String template, int page, int pageSize) {
        return db.searchTemplate(template, page, pageSize);
    }

    @Override
    public List<String> predicates(String keyword) {
        return db.searchPredicate(keyword, 10);
    }

    @Override
    public TemplateData insert(String template, String property, @Valid MapRuleData data) {
        final MapRule rule = data.fill(null);
        TemplateMapping t = db.read(template);
        if (t == null)
            t = new TemplateMapping(template);
        if (property == null) {
            t.getRules().remove(rule);
            t.getRules().add(rule);
        } else {
            PropertyMapping p = null;
            for (PropertyMapping p2 : t.getProperties())
                if (p2.getProperty().equals(property))
                    p = p2;
            if (p == null) {
                p = new PropertyMapping(template, property);
                t.getProperties().add(p);
            }
            p.getRules().remove(rule);
            p.getRules().add(rule);
        }
        db.write(t);
        return new TemplateData().sync(t);
    }

    @Override
    public TemplateData delete(String template, String property, @Valid MapRuleData data) {
        final MapRule rule = data.fill(null);
        final TemplateMapping t = db.read(template);
        if (t == null) return null;
        if (property == null)
            t.getRules().remove(rule);
        else {
            PropertyMapping p = null;
            for (PropertyMapping p2 : t.getProperties())
                if (p2.getProperty().equals(property))
                    p = p2;
            if (p == null) return null;
            p.getRules().remove(rule);
        }
        db.write(t);
        return new TemplateData().sync(t);
    }
}
