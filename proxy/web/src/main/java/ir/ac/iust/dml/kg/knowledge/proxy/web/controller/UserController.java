package ir.ac.iust.dml.kg.knowledge.proxy.web.controller;

import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IPermissionDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * Controller for user creation
 */
@Controller
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final IUserDao userDao;
    private final IPermissionDao permissionDao;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, IUserDao userDao, IPermissionDao permissionDao) {
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        this.permissionDao = permissionDao;
    }

    @PostConstruct
    public void setup() {
        if (permissionDao.readByTitle("User") == null)
            permissionDao.write(new Permission("User", " Manage users of proxy server"));
        if (permissionDao.readByTitle("Forward") == null)
            permissionDao.write(new Permission("Forward", " Manage forward of proxy server"));
        if (userDao.readByUsername("superuser") == null)
            userDao.write(new User("superuser", passwordEncoder.encode("superuser"), "superuser"));
    }
}
