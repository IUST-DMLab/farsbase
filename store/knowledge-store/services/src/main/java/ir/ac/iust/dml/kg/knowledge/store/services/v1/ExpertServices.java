package ir.ac.iust.dml.kg.knowledge.store.services.v1;

import ir.ac.iust.dml.kg.knowledge.store.access.dao.ITripleDao;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.ExpertVote;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Triple;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Vote;
import ir.ac.iust.dml.kg.knowledge.store.services.ExpertLogic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.util.List;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link IExpertServices}
 */
@Deprecated
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.store.services.v1.IExpertServices")
public class ExpertServices implements IExpertServices {
    @Autowired
    private ITripleDao dao;

    @Override
    public List<Triple> triples(String module, String expert, int count) {
        return dao.randomTripleForExpert(module, expert, count);
    }

    @Override
    public Boolean vote(String identifier, String module, String expert, Vote vote) {
        final Triple triple = dao.read(new ObjectId(identifier));
        if (triple == null) return false;
        final ExpertVote v = new ExpertVote(module, expert, vote);
        triple.getVotes().remove(v);
        triple.getVotes().add(v);
        triple.setState(ExpertLogic.makeState(triple.getVotes()));
        dao.write(triple);
        return true;
    }

    @Override
    public List<Triple> triplesSubject(String sourceModule, String module, String expert, String subjectQuery, String subjectMatch, Integer size) {
        return dao.randomSubjectForExpert(sourceModule, module, expert, subjectQuery, subjectMatch, size != null && size > 0 ? size : null);
    }
}
