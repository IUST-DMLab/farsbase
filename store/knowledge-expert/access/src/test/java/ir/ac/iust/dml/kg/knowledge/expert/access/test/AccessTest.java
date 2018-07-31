package ir.ac.iust.dml.kg.knowledge.expert.access.test;

import ir.ac.iust.dml.kg.knowledge.expert.access.dao.ITicketDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.UserPermission;
import ir.ac.iust.dml.kg.knowledge.store.client.Triple;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for access
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:persistence-context.xml")
public class AccessTest {
    @Autowired
    IUserDao userDao;
    @Autowired
    ITicketDao ticketDao;

    final User defUser1 = new User("#defUser1", "pass1", "name1", UserPermission.Expert);

    @Before
    public void setup() {
        userDao.write(defUser1);
    }

    @After
    public void cleanup() {
        userDao.delete(defUser1);
    }

    @Test
    public void testUserDao() {
        final User user1 = new User("user1", "pass1", "name1", UserPermission.Expert);
        final User user2 = new User("user2", "pass2", "name2", UserPermission.Expert);
        userDao.write(user1, user2);
        try {
            userDao.write(new User("user1", "pass1", "name1", UserPermission.Expert));
            assert false;
        } catch (Throwable th) {
            assert true;
        }
        assert userDao.read(user1.getId()).getUsername().equals(user1.getUsername());
        userDao.delete(user1, user2);
    }

    @Test
    public void testTicketDao() {
        final Ticket ticket1 = new Ticket(new Triple("triple1"), defUser1);
        final Ticket ticket2 = new Ticket(new Triple("triple2"), defUser1);
        ticketDao.write(ticket1, ticket2);
        try {
            ticketDao.write(new Ticket(new Triple("triple1"), defUser1));
            assert false;
        } catch (Throwable th) {
            assert true;
        }
        assert ticketDao.readAssignedTicket(defUser1, null, 0, 0).getTotalSize() == 2;
        ticket2.setVote(Vote.Approve);
        ticketDao.write(ticket2);
        assert ticketDao.readAssignedTicket(defUser1, null, 0, 0).getTotalSize() == 1;
        assert ticketDao.readAssignedTicket(defUser1, null, 0, 0).getData().get(0).getIdentifier().equals(ticket1.getIdentifier());
        ticketDao.delete(ticket1, ticket2);
    }
}
