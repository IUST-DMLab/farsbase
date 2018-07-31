package ir.ac.iust.dml.kg.knowledge.runner.access.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Each definition is list of command that run sequentially
 * Job is done if all run successfully
 */
@XmlType(name = "Definition", namespace = "http://kg.dml.iust.ac.ir")
@Document(collection = "definitions")
public class Definition {
    @Id
    @JsonIgnore
    private ObjectId id;
    @Indexed(unique = true)
    private String title;
    private List<CommandLine> commands;
    private long creationEpoch;
    private Integer maxTryCount;
    private Long maxTryDuration;

    public Definition() {
    }

    public Definition(String title, List<CommandLine> commands) {
        this.creationEpoch = System.currentTimeMillis();
        this.title = title;
        this.commands = commands;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommandLine> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandLine> commands) {
        this.commands = commands;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public String getIdentifier() {
        return id != null ? id.toString() : null;
    }

    public Integer getMaxTryCount() {
        return maxTryCount;
    }

    public void setMaxTryCount(Integer maxTryCount) {
        this.maxTryCount = maxTryCount;
    }

    public Long getMaxTryDuration() {
        return maxTryDuration;
    }

    public void setMaxTryDuration(Long maxTryDuration) {
        this.maxTryDuration = maxTryDuration;
    }

    @Override
    public String toString() {
        return String.format("Definition{%s, %s}", id, title);
    }
}
