package ir.ac.iust.dml.kg.knowledge.proxy.access.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * User of mongo
 */
@Document(collection = "users")
@XmlType(name = "User", namespace = "http://kg.dml.iust.ac.ir")
public class User implements Serializable {
    @Id
    @JsonIgnore
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    private String name;
    @DBRef
    private Set<Permission> permissions;

    public User() {
    }

    public User(String username, String password, String name, Permission... permissions) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.permissions = new HashSet<>();
        Collections.addAll(this.permissions, permissions);
    }

    public String getIdentifier() {
        return id != null ? id.toString() : null;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public Set<Permission> getPermissions() {
        if (permissions == null) permissions = new HashSet<>();
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
