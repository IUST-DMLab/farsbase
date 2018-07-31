package ir.ac.iust.dml.kg.knowledge.expert.access.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import org.bson.types.ObjectId;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Interface dao for ticket entity operation
 */
public interface ITicketDao {
    void write(Ticket... tickets);

    void delete(Ticket... tickets);

    Ticket read(ObjectId id);

    Ticket read(User user, String identifier);

    PagingList<Ticket> readAssignedTicket(User user, String subject, int page, int pageSize);

    PagingList<KeyCount> readAssignedSubjects(User user, int page, int pageSize);
}
