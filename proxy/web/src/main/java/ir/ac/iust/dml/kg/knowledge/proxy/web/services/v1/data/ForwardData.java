package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.UrnMatching;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data of forward proxy
 */
public class ForwardData {
    @ApiModelProperty(required = true)
    private String identifier;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "\\p{javaLowerCase}+")
    @ApiModelProperty(required = true, example = "store")
    private String source;

    @Valid
    @ApiModelProperty("List of urn that security must be applied to it")
    private List<UrnMatchingData> urns;

    @NotNull
    @NotEmpty
    @URL
    @ApiModelProperty(required = true, example = "http://localhost:8081/services")
    private String destination;

    private Set<String> permissions;

    public Forward fill(Forward forward) {
        if (forward == null) forward = new Forward();
        else assert identifier.equals(forward.getIdentifier());
        forward.setUrns(new ArrayList<>());
        if (urns != null) {
            for (UrnMatchingData u : urns) {
                permissions.addAll(u.getPermissions());
                forward.getUrns().add(u.fill(null));
            }
        }
        forward.setSource(source);
        forward.setDestination(destination);
        return forward;
    }

    public ForwardData sync(Forward forward) {
        this.identifier = forward.getIdentifier();
        this.source = forward.getSource();
        this.destination = forward.getDestination();
        this.permissions = new HashSet<>();
        forward.getPermissions().forEach(f -> this.permissions.add(f.getTitle()));
        this.urns = new ArrayList<>();
        for (UrnMatching u : forward.getUrns()) {
            this.urns.add(new UrnMatchingData().sync(u));
        }
        return this;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public List<UrnMatchingData> getUrns() {
        return urns;
    }

    public void setUrns(List<UrnMatchingData> urns) {
        this.urns = urns;
    }
}
