package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data;

import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Data of user
 */
@XmlType(name = "ProfileData")
public class ProfileData {
    private String username;
    @Size(min = 3)
    private String currentPassword;
    @Size(min = 3)
    private String newPassword;
    @Size(min = 2)
    private String name;
    private Set<String> permissions;

    public User fill(User user) {
        if (newPassword != null)
            user.setPassword(newPassword);
        if (name != null)
            user.setName(name);
        return user;
    }

    public ProfileData sync(User user) {
        newPassword = currentPassword = null;
        username = user.getUsername();
        name = user.getName();
        permissions = new HashSet<>();
        user.getPermissions().forEach(p -> permissions.add(p.getTitle()));
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
