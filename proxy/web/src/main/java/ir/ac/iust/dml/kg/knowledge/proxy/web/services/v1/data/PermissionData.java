package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data;

import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlType;

/**
 * Data of user
 */
@XmlType(name = "PermissionData")
public class PermissionData {
    private String identifier;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "\\p{javaAlphabetic}+")
    private String title;
    private String description;

    public Permission fill(Permission permission) {
        if (permission == null) permission = new Permission();
        else assert identifier.equals(permission.getIdentifier());
        permission.setTitle(title);
        permission.setDescription(description);
        return permission;
    }

    public PermissionData sync(Permission permission) {
        identifier = permission.getIdentifier();
        title = permission.getTitle();
        description = permission.getDescription();
        return this;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
