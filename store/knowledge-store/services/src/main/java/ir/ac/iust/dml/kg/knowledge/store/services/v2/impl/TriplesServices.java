package ir.ac.iust.dml.kg.knowledge.store.services.v2.impl;

import ir.ac.iust.dml.kg.knowledge.store.access2.dao.ISubjectDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IVersionDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Subject;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleObject;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Version;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.ITriplesServices;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.TripleData;
import ir.ac.iust.dml.kg.raw.utils.URIs;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link ITriplesServices}
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v2.ITriplesServices")
public class TriplesServices implements ITriplesServices {
    @Autowired
    private ISubjectDao dao;
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
        if (data.getContext() == null) data.setContext(URIs.INSTANCE.getDefaultContext());
        if (data.getVersion() == null && data.getModule() != null) {
            final Version version = versionMap.get(data.getModule());
            if (version != null) data.setVersion(version.getNextVersion());
        }
        final Subject oldSubject = dao.read(data.getContext(), data.getSubject());
        final Subject newSubject = data.fill(oldSubject);
        newSubject.updateSourcesNeedVote(); // TripleData can approve some triple
        dao.write(newSubject);
        return true;
    }

    @Override
    public Boolean batchInsert(@Valid List<TripleData> data) {
        Map<String, Subject> effectedSubjects = new HashMap<>();
        data.forEach(d -> {
            if (d.getContext() == null) d.setContext(URIs.INSTANCE.getDefaultContext());
            if (d.getVersion() == null && d.getModule() != null) {
                final Version version = versionMap.get(d.getModule());
                if (version != null) d.setVersion(version.getNextVersion());
            }
            final Subject mapSubject = effectedSubjects.get(String.format("%s#%s", d.getContext(), d.getSubject()));
            final Subject oldSubject = mapSubject != null ? mapSubject : dao.read(d.getContext(), d.getSubject());
            final Subject newSubject = d.fill(oldSubject);
            if (mapSubject == null)
                effectedSubjects.put(String.format("%s#%s", d.getContext(), d.getSubject()), newSubject);
        });
        effectedSubjects.forEach((k, v) -> {
            v.updateSourcesNeedVote();
            dao.write(v);
        });
        return true;
    }

    @Override
    public List<TripleData> remove(String context, String subject, String predicate, String object) {
        if (context == null) context = URIs.INSTANCE.getDefaultContext();
        final List<TripleData> removed = new ArrayList<>();
        final Subject dbSubject = dao.read(context, subject);
        if (dbSubject == null) return removed;
        final List<TripleObject> current = dbSubject.getTriples().get("predicate");
        if (current == null) return removed;
        for (int j = current.size() - 1; j >= 0; j--) {
            final TripleObject tripleObject = current.get(j);
            if (tripleObject.getValue().equals(object)) {
                current.remove(j);
                removed.add(new TripleData().sync(context, subject, predicate, tripleObject));
            }
        }
        dao.write(dbSubject);
        return removed;
    }

    @Override
    public List<TripleData> triple(String context, String subject, String predicate, String object) {
        if (context == null) context = URIs.INSTANCE.getDefaultContext();
        final List<TripleData> result = new ArrayList<>();
        final Subject dbSubject = dao.read(context, subject);
        if (dbSubject == null) return result;
        final List<TripleObject> current = dbSubject.getTriples().get("predicate");
        if (current == null) return result;
        for (int j = current.size() - 1; j >= 0; j--) {
            final TripleObject tripleObject = current.get(j);
            if (tripleObject.getValue().equals(object))
                result.add(new TripleData().sync(context, subject, predicate, tripleObject));
        }
        return result;
    }


}
