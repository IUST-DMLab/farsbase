package ir.ac.iust.dml.kg.knowledge.proxy.access.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Forward definition to forward req from source to destination
 */
@Document(collection = "forwards")
public class Forward implements Serializable {
    @Id
    @JsonIgnore
    private ObjectId id;
    @Indexed(unique = true)
    private String source;
    private String destination;
    private List<UrnMatching> urns;
    @DBRef
    private Set<Permission> permissions;

    public Forward() {
    }

    public Forward(String source, String destination, Permission... permissions) {
        this.source = source;
        this.destination = destination;
        this.permissions = new HashSet<>();
        Collections.addAll(this.permissions, permissions);
    }

    public UrnMatching match(String urn, String method) {
        if (urns != null)
            for (UrnMatching u : urns)
                if (u.match(urn, method))
                    return u;
        return null;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<UrnMatching> getUrns() {
        if (urns == null) urns = new ArrayList<>();
        return urns;
    }

    public void setUrns(List<UrnMatching> urns) {
        this.urns = urns;
    }

    public Set<Permission> getPermissions() {
        if (permissions == null) permissions = new HashSet<>();
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Forward forward = (Forward) o;

        if (source != null ? !source.equals(forward.source) : forward.source != null) return false;
        return destination != null ? destination.equals(forward.destination) : forward.destination == null;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }

}
