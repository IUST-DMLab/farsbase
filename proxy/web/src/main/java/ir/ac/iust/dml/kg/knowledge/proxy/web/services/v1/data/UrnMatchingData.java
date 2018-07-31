package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.MatchingType;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.UrnMatching;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Data of urn
 */
@Document(collection = "UrnPermissionData")
public class UrnMatchingData {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true)
    private String urn;
    @NotNull
    @ApiModelProperty(required = true, value = "Type of urn matching method")
    private MatchingType type;
    @ApiModelProperty(required = false, example = "POST", value = "If be null check for all web method ")
    private String method;
    @NotNull
    private Set<String> permissions;

    UrnMatching fill(UrnMatching p) {
        if (p == null)
            p = new UrnMatching();
        p.setUrn(urn);
        p.setType(type);
        p.setMethod(method != null ? method.toUpperCase() : null);
        return p;
    }

    UrnMatchingData sync(UrnMatching p) {
        urn = p.getUrn();
        type = p.getType();
        method = p.getMethod();
        permissions = new HashSet<>();
        p.getPermissions().forEach(i -> permissions.add(i.getTitle()));
        return this;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public MatchingType getType() {
        return type;
    }

    public void setType(MatchingType type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
