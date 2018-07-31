package ir.ac.iust.dml.kg.knowledge.expert.web.services.v1;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IReportDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserStats;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserVoteStats;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.expert.web.services.v1.IReportService")
public class ReportsServiceImpl implements IReportService {
    @Autowired
    private IUserDao userDao;
    @Autowired
    private IReportDao reportDao;

    @Override
    public List<String> login() {
        final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        final List<String> result = new ArrayList<>();
        authorities.forEach(a -> result.add(a.getAuthority()));
        return result;
    }

    @Override
    public PagingList<Ticket> triples(String username, String subject, Boolean hasVote, Vote vote, int page, int pageSize) {
        User user = username != null ? userDao.readByUsername(username) : null;
        return reportDao.searchTicketState(user, subject, hasVote, vote, page, pageSize);
    }

    @Override
    public PagingList<KeyCount> countBySubject(String username, Boolean hasVote, Vote vote, int page, int pageSize) {
        User user = username != null ? userDao.readByUsername(username) : null;
        return reportDao.countBySubject(user, hasVote, vote, page, pageSize);
    }

    @Override
    public PagingList<UserStats> countByUser(String username, Boolean hasVote, Vote vote, int page, int pageSize) {
        User user = username != null ? userDao.readByUsername(username) : null;
        return reportDao.countByUser(user, hasVote, vote, page, pageSize);
    }

    @Override
    public PagingList<UserVoteStats> countByUserVote(String username, Boolean hasVote, Vote vote, int page, int pageSize) {
        User user = username != null ? userDao.readByUsername(username) : null;
        return reportDao.countByUserVote(user, hasVote, vote, page, pageSize);
    }
}
