package ir.ac.iust.dml.kg.knowledge.expert.web.services.v1;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.web.services.v1.data.UserData;
import ir.ac.iust.dml.kg.knowledge.store.client.V1StoreClient;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.jws.WebService;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Impl {@link IExpertServices}
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.expert.web.services.v1.IUserServices")
public class UserServiceImpl implements IUserServices {
    @Autowired
    private IUserDao userDao;
    @Autowired
    private V1StoreClient client;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<String> login() {
        final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        final List<String> result = new ArrayList<>();
        authorities.forEach(a -> result.add(a.getAuthority()));
        return result;
    }

    @Override
    public User edit(@Valid UserData data) {
        User user = data.getIdentifier() == null ? new User() : userDao.read(new ObjectId(data.getIdentifier()));
        if (user == null) return null;
        User oldUser = userDao.readByUsername(data.getUsername());
        if (oldUser != null && !oldUser.getIdentifier().equals(user.getIdentifier())) return null;
        if (data.getPassword() != null) data.setPassword(passwordEncoder.encode(data.getPassword()));
        data.fill(user);
        userDao.write(user);
        return user;
    }


    @Override
    public PagingList<User> search(String name, String username, int page, int pageSize) {
        return userDao.search(name, username, page, pageSize);
    }

}
