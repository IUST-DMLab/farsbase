package ir.ac.iust.dml.kg.knowledge.store.services.v1;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.store.access.dao.ITripleDao;
import ir.ac.iust.dml.kg.knowledge.store.access.dao.IVersionDao;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Source;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Triple;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Version;
import ir.ac.iust.dml.kg.knowledge.store.services.v1.data.TripleData;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * impl {@link ITriplesServices}
 */
@Deprecated
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v1.ITriplesServices")
public class TriplesServices implements ITriplesServices {
    @Autowired
    private ITripleDao dao;
    @Autowired
    private IVersionDao versionDao;
    private Map<String, Version> versionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void setup() {
        final List<Version> versions = versionDao.readAll();
        versions.forEach(v -> versionMap.put(v.getModule(), v));
    }

    @Override
    public Integer newVersion(String module) {
        synchronized (this) {
            Version version = versionMap.get(module);
            if (version == null) {
                version = new Version(module);
                versionMap.put(module, version);
            } else
                version.addNextVersion();
            versionDao.write(version);
            return version.getNextVersion();
        }
    }

    @Override
    public Boolean activateVersion(String module, Integer version) {
        synchronized (this) {
            final Version db = versionMap.get(module);
            if (db == null) return false;
            db.setActiveVersion(version != null ? version : db.getNextVersion());
            versionDao.write(db);
            return true;
        }
    }

    @Override
    public Boolean insert(@Valid TripleData data) {
        if (data.getContext() == null) data.setContext("http://kg.dml.iust.ac.ir");
        if (data.getVersion() == null && data.getModule() != null) {
            final Version version = versionMap.get(data.getModule());
            if (version != null) data.setVersion(version.getNextVersion());
        }
        final Triple oldTriple = dao.read(data.getContext(), data.getSubject(), data.getPredicate(), data.getObject().getValue());
        final Triple newTriple = data.fill(oldTriple);
        dao.write(newTriple);
        return true;
    }

    @Override
    public Boolean batchInsert(@Valid List<TripleData> data) {
        data.forEach(this::insert);
        return true;
    }

    @Override
    public Triple remove(String context, String subject, String predicate, String object) {
        if (context == null) context = "http://kg.dml.iust.ac.ir";
        Triple triple = dao.read(context, subject, predicate, object);
        if (triple != null)
            dao.delete(triple);
        return triple;
    }

    @Override
    public Triple triple(String context, String subject, String predicate, String object) {
        if (context == null) context = "http://kg.dml.iust.ac.ir";
        return dao.read(context, subject, predicate, object);
    }

    @Override
    public PagingList<Triple> search(String context, Boolean useRegexForContext,
                                     String subject, Boolean useRegexForSubject,
                                     String predicate, Boolean useRegexForPredicate,
                                     String object, Boolean useRegexForObject,
                                     int page, int pageSize) {
        return dao.search(
            context, useRegexForContext != null && useRegexForContext,
            subject, useRegexForSubject != null && useRegexForSubject,
            predicate, useRegexForPredicate != null && useRegexForPredicate,
            object, useRegexForObject != null && useRegexForObject,
            page, pageSize);
    }

    @Override
    public String export(TripleState state, ExportFormat format, Long epoch, int page, int pageSize) {


        final PagingList<Triple> triples = dao.read(state, epoch, page, pageSize);
        final ModelBuilder builder = new ModelBuilder();
        triples.getData().forEach(t -> {
            boolean isLatestVersion = t.getSources().isEmpty();
            for (Source s : t.getSources()) {
                Version sv = versionMap.get(s.getModule());
                if (sv == null || Objects.equals(sv.getActiveVersion(), s.getVersion()))
                    isLatestVersion = true;
            }
            if (isLatestVersion && hasValidURIs(t))
                builder.add(t.getSubject(), t.getPredicate(), createValue(t.getObject()));
        });

        final Model model = builder.build();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final RDFWriter writer = Rio.createWriter(format.getRDFFormat(), out);
        writer.startRDF();
        writer.handleNamespace("kg", "http://knowledgegraph.ir/");
        model.forEach(writer::handleStatement);
        writer.endRDF();
        return out.toString();
    }

    private boolean hasValidURIs(Triple triple) {
        try {
            new URL(triple.getSubject());
            new URL(triple.getPredicate());
            if (triple.getObject().getType() == ValueType.Resource)
                new URL(triple.getObject().getValue());
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object createValue(TypedValue v) {
        final ValueFactory vf = SimpleValueFactory.getInstance();
        if (v.getType() != null)
            switch (v.getType()) {
                case Resource:
                    return vf.createIRI(v.getValue());
                case String:
                    return vf.createLiteral(v.getValue(), v.getLang());
                case Boolean:
                    return vf.createLiteral(v.getValue(), XMLSchema.BOOLEAN);
                case Byte:
                    return vf.createLiteral(v.getValue(), XMLSchema.BYTE);
                case Short:
                    return vf.createLiteral(v.getValue(), XMLSchema.SHORT);
                case Integer:
                    return vf.createLiteral(v.getValue(), XMLSchema.INTEGER);
                case Long:
                    return vf.createLiteral(v.getValue(), XMLSchema.LONG);
                case Double:
                    return vf.createLiteral(v.getValue(), XMLSchema.DOUBLE);
                case Float:
                    return vf.createLiteral(v.getValue(), XMLSchema.FLOAT);
                case Date:
                    return vf.createLiteral(new Date(Long.parseLong(v.getValue())));
            }
        return vf.createLiteral(v.getValue());
    }
}
