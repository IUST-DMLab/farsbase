package ir.ac.iust.dml.kg.knowledge.runner.access.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * data class for Run
 * A definition run multiple time
 * Every time data stored as Run
 */
@XmlType(name = "Run", namespace = "http://kg.dml.iust.ac.ir")
@Document(collection = "runs")
public class Run {
    @Id
    @JsonIgnore
    private ObjectId id;
    @Indexed
    private String title;
    private List<CommandLine> commands;
    private long creationEpoch;
    private Integer remindedTryCount;
    private Long validUntilEpoch;
    private Long startEpoch;
    private Float progress;
    private Long endEpoch;
    @Indexed
    private RunState state;
    @DBRef
    private Definition definition;

    public Run() {
    }

    public Run(String title, List<CommandLine> commands) {
        this.creationEpoch = System.currentTimeMillis();
        this.title = title;
        this.commands = commands;
    }

    public Run(Definition def) {
        title = def.getTitle();
        commands = def.getCommands();
        creationEpoch = System.currentTimeMillis();
        remindedTryCount = def.getMaxTryCount();
        validUntilEpoch = def.getMaxTryDuration();
        if(validUntilEpoch != null) validUntilEpoch += System.currentTimeMillis();
        definition = def;
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

    public Long getStartEpoch() {
        return startEpoch;
    }

    public void setStartEpoch(Long startEpoch) {
        this.startEpoch = startEpoch;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public Long getEndEpoch() {
        return endEpoch;
    }

    public void setEndEpoch(Long endEpoch) {
        this.endEpoch = endEpoch;
    }

    public RunState getState() {
        return state;
    }

    public void setState(RunState state) {
        this.state = state;
    }

    public String getIdentifier() {
        return id != null ? id.toString() : null;
    }

    public Integer getRemindedTryCount() {
        return remindedTryCount;
    }

    public void setRemindedTryCount(Integer remindedTryCount) {
        this.remindedTryCount = remindedTryCount;
    }

    public Long getValidUntilEpoch() {
        return validUntilEpoch;
    }

    public void setValidUntilEpoch(Long validUntilEpoch) {
        this.validUntilEpoch = validUntilEpoch;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return String.format("Run{%s, %s}", id, title);
    }
}

