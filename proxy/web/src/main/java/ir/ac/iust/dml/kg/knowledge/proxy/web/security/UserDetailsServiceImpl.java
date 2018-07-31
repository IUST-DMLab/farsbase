package ir.ac.iust.dml.kg.knowledge.proxy.web.security;

import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IPermissionDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * implements UserDetailsService to read user
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final IUserDao users;
    private final IPermissionDao permissions;

    @Autowired
    public UserDetailsServiceImpl(IUserDao users, IPermissionDao permissions) {
        this.users = users;
        this.permissions = permissions;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = users.readByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");
        if (user.getUsername().equals("superuser"))
            user.getPermissions().addAll(permissions.readAll());
        return new MyUserDetails(user);
    }
}
