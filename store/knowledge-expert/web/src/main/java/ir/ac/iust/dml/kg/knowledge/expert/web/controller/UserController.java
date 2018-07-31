package ir.ac.iust.dml.kg.knowledge.expert.web.controller;

import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * Controller for user creation
 */
@Controller
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserDao userDao;

    @PostConstruct
    public void setup() {
        if (userDao.readByUsername("superuser") == null)
            userDao.write(new User("superuser", passwordEncoder.encode("superuser"),
                    "superuser", UserPermission.Superuser));
        if (userDao.readByUsername("expert") == null)
            userDao.write(new User("expert", passwordEncoder.encode("expert"),
                    "superuser", UserPermission.Expert));
        if (userDao.readByUsername("vip") == null)
            userDao.write(new User("vip", passwordEncoder.encode("vip"),
                    "superuser", UserPermission.VIPExpert));
    }
}
