package ir.ac.iust.dml.kg.knowledge.expert.web.services.v1.data;

import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.UserPermission;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Data of user
 */
@XmlType(name = "UserData")
public class UserData {
    private String identifier;
    @NotNull
    @NotEmpty
    private String username;
    @NotEmpty
    @Size(min = 3)
    private String password;
    @NotEmpty
    @Size(min = 2)
    private String name;
    private Set<UserPermission> permissions;

    public User fill(User user) {
        if (user == null) user = new User();
        else assert identifier.equals(user.getId().toString());
        if (user.getUsername() == null || !user.getUsername().equals("superuser"))
            user.setUsername(username);
        if (password != null)
            user.setPassword(password);
        if (!username.equals("superuser")) {
            user.getPermissions().clear();
            permissions.remove(UserPermission.Superuser);
            user.setPermissions(permissions);
        }
        user.setName(name);
        return user;
    }

    public UserData sync(User user) {
        identifier = user.getIdentifier();
        username = user.getUsername();
        name = user.getName();
        permissions = user.getPermissions();
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }
}
