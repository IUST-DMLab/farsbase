package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1;

import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.proxy.web.security.MyUserDetails;
import ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data.ProfileData;
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
 * Impl {@link IUserServices}
 */
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.IProfileServices")
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "Duplicates"})
public class ProfileServiceImpl implements IProfileServices {
    @Autowired
    private IUserDao userDao;
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
    public ProfileData edit(@Valid ProfileData data) {
        final User user = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (data.getUsername() != null && !user.getUsername().equals(data.getUsername()))
            throw new RuntimeException("Username can not be changed");
        if (data.getNewPassword() != null && passwordEncoder.matches(data.getCurrentPassword(), user.getPassword()))
            data.setNewPassword(passwordEncoder.encode(data.getNewPassword()));
        else if (data.getNewPassword() != null)
            throw new RuntimeException("Current password is not correct");
        data.fill(user);
        userDao.write(user);
        return data.sync(user);
    }


}
