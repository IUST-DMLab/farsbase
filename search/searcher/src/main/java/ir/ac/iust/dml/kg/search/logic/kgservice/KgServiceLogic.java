package ir.ac.iust.dml.kg.search.logic.kgservice;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.URIs;
import ir.ac.iust.dml.kg.search.logic.kgservice.data.*;
import ir.ac.iust.dml.kg.virtuoso.connector.VirtuosoConnector;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleType;

import java.util.List;

public class KgServiceLogic {

    private final VirtuosoConnector connector;

    public KgServiceLogic() {
        System.err.println("Loading KgServiceLogic ...");
        long t1 = System.currentTimeMillis();
        final String virtuosoServer = ConfigReader.INSTANCE.getString("virtuoso.address", "localhost:1111");
        final String virtuosoUser = ConfigReader.INSTANCE.getString("virtuoso.user", "user_e_SHOMA");
        final String virtuosoPass = ConfigReader.INSTANCE.getString("virtuoso.password", "password_e_SHOMA");
      connector = new VirtuosoConnector(ConfigReader.INSTANCE.getString("virtuoso.graph", "http://fkg.iust.ac.ir"),
          virtuosoServer, virtuosoUser, virtuosoPass);
        System.err.printf("KgServiceLogic loaded in %,d ms", (System.currentTimeMillis() - t1));
    }

    private String getLabel(String url) {
        if (url == null) return null;
      final String defaultLabel = url.substring(url.lastIndexOf('/') + 1)
          .replace('_', ' ');
      final List<VirtuosoTriple> parent = connector.getTriples(url, URIs.INSTANCE.getLabel());
      if (parent == null || parent.isEmpty() || parent.get(0).getObject() == null) return defaultLabel;
        final Object label = parent.get(0).getObject().getValue();
      if (label == null) return defaultLabel;
        return label.toString();
    }

    public ParentNode getParent(String childUrl) {
      final List<VirtuosoTriple> parent = connector.getTriples(childUrl, URIs.INSTANCE.getSubClassOf());
        if (parent == null || parent.isEmpty() || parent.get(0).getObject() == null) return null;
        final String parentUrl = parent.get(0).getObject().getValue().toString();
        return new ParentNode(parentUrl, getLabel(parentUrl));
    }

    public ChildNodes getChildren(String parentUrl) {
      final List<VirtuosoTriple> children = connector.getTriplesOfObject(URIs.INSTANCE.getSubClassOf(), parentUrl);
        if (children == null || children.isEmpty()) return null;
        final ChildNodes result = new ChildNodes();
        for (VirtuosoTriple triple : children) {
            if (triple.getSource() == null) continue;
            result.getChildNodes().add(new ChildNode(triple.getSource(), getLabel(triple.getSource())));
        }
        return result;
    }

    public ClassInfo getClassInfo(String url) {
        final ClassInfo classData = new ClassInfo(getLabel(url));
      final List<VirtuosoTriple> triples = connector.getTriplesOfObject(URIs.INSTANCE.getPropertyDomain(), url);
        if (triples == null || triples.isEmpty()) return null;
        for (VirtuosoTriple triple : triples) {
            if (triple.getPredicate() == null || triple.getSource() == null) continue;
            classData.getProperties().add(new PropertyInfo(triple.getSource(),
                    getLabel(triple.getSource()), false));
        }
        return classData;
    }

    public EntityData getEntityInfo(String url) {
        final EntityData entityData = new EntityData(getLabel(url));
        final List<VirtuosoTriple> triples = connector.getTriplesOfSubject(url);
        if (triples == null || triples.isEmpty()) return null;
        for (VirtuosoTriple triple : triples) {
            if (triple.getPredicate() == null || triple.getObject() == null || triple.getObject() == null) continue;
            final String predicateLabel = getLabel(triple.getPredicate());
            if (predicateLabel == null) continue;
            final PropertyValue value = new PropertyValue();
            if (triple.getObject().getType() == VirtuosoTripleType.Resource)
                value.setUrl(triple.getObject().getValue().toString());
            else value.setContent(triple.getObject().getValue().toString());
            final PropertyData data = entityData.getPropertyMap().get(triple.getPredicate());
            if(data != null) data.getPropValue().add(value);
            else entityData.getPropertyMap().put(triple.getPredicate(),
                    new PropertyData(triple.getPredicate(), predicateLabel, value));
        }
        return entityData;
    }

    public Entities getEntitiesOfClass(String classUrl, int page, int pageSize) {
        if (pageSize > 1000) pageSize = 1000;
        final Entities entities = new Entities();
      final List<VirtuosoTriple> triples = connector.getTriplesOfObject(URIs.INSTANCE.getType(), classUrl, page, pageSize);
        if (triples == null) return null;
        for (VirtuosoTriple triple : triples) {
            if (triple.getPredicate() == null || triple.getSource() == null) continue;
            final String label = getLabel(triple.getSource());
            if (label == null) continue;
            entities.getResult().add(new EntityInfo(triple.getSource(), label));
        }
        return entities;
    }

    public EntityClasses getEntityClasses(String url) {
        final EntityClasses entityClasses = new EntityClasses();
      List<VirtuosoTriple> triples = connector.getTriples(url, URIs.INSTANCE.getInstanceOf());
        if (triples == null || triples.isEmpty() || triples.get(0).getObject() == null
            || triples.get(0).getObject().getValue() == null) return null;
        entityClasses.setMainClass(triples.get(0).getObject().getValue().toString());
      triples = connector.getTriples(url, URIs.INSTANCE.getType());
        for (VirtuosoTriple triple : triples) {
            if (triple.getPredicate() == null || triple.getObject() == null || triple.getObject().getValue() == null)
                continue;
            final String c = triple.getObject().getValue().toString();
          if (!c.equals(URIs.INSTANCE.getTypeOfAllResources()))
                entityClasses.getClassTree().add(c);
        }
        return entityClasses;
    }
}
