package ir.ac.iust.dml.kg.knowledge.expert.access.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserStats;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserVoteStats;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Dao for reports of expert activities
 */
public interface IReportDao {
    PagingList<Ticket> searchTicketState(User user, String subject,
                                         Boolean hasVote, Vote vote,
                                         int page, int pageSize);

    PagingList<KeyCount> countBySubject(User user,
                                        Boolean hasVote, Vote vote,
                                        int page, int pageSize);

    PagingList<UserStats> countByUser(User user,
                                      Boolean hasVote, Vote vote,
                                      int page, int pageSize);

    PagingList<UserVoteStats> countByUserVote(User user,
                                              Boolean hasVote, Vote vote,
                                              int page, int pageSize);
}
