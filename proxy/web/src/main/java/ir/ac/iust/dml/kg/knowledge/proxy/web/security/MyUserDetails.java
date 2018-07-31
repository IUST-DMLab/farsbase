package ir.ac.iust.dml.kg.knowledge.proxy.web.security;

import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User detail to be stored in session
 */
public class MyUserDetails implements UserDetails {
    private final User user;

    MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> authorities = new HashSet<>();
        for (Permission p : user.getPermissions())
            authorities.add(new SimpleGrantedAuthority(p.getTitle()));
        return authorities;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
