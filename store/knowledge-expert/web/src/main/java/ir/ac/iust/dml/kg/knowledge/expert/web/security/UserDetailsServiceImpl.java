package ir.ac.iust.dml.kg.knowledge.expert.web.security;

import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * implements UserDetailsService to read user
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private IUserDao Autowired;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = Autowired.readByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");
        return new MyUserDetails(user);
    }
}
