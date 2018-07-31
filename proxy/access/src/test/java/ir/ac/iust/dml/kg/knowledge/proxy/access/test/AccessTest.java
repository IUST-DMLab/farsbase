package ir.ac.iust.dml.kg.knowledge.proxy.access.test;

import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IForwardDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IPermissionDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
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
    IUserDao users;
    @Autowired
    IPermissionDao permissions;
    @Autowired
    IForwardDao forwardDao;

    final Permission defPer1 = new Permission("test", "a test permission");
    final User defUser1 = new User("#defUser1", "pass1", "name1", defPer1);

    @Before
    public void setup() {
        permissions.write(defPer1);
        users.write(defUser1);
    }

    @After
    public void cleanup() {
        users.delete(defUser1);
        permissions.delete(defPer1);
    }

    @Test
    public void testUserDao() {
        final User user1 = new User("user1", "pass1", "name1", defPer1);
        final User user2 = new User("user2", "pass2", "name2", defPer1);
        users.write(user1, user2);
        try {
            users.write(new User("user1", "pass1", "name1", defPer1));
            assert false;
        } catch (Throwable th) {
            assert true;
        }
        assert users.read(user1.getId()).getUsername().equals(user1.getUsername());
        users.delete(user1, user2);
    }

    @Test
    public void testPermissionDao() {
        final Permission per1 = new Permission("per1");
        final Permission per2 = new Permission("per2");
        permissions.write(per1, per2);
        try {
            permissions.write(new Permission("per1"));
            assert false;
        } catch (Throwable th) {
            assert true;
        }
        assert permissions.read(per1.getId()).getTitle().equals(per1.getTitle());
        assert permissions.readAll().contains(per2);
        permissions.delete(per1, per2);
    }

    @Test
    public void testForwardDao() {
        final Forward for1 = new Forward("c1", "f1");
        final Forward for2 = new Forward("c2", "f2");
        forwardDao.write(for1, for2);
        try {
            forwardDao.write(new Forward("c1", "f1"));
            assert false;
        } catch (Throwable th) {
            assert true;
        }
        assert forwardDao.readAll().contains(for1);
        forwardDao.delete(for1, for2);

    }
}
