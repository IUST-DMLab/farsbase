package ir.ac.iust.dml.kg.knowledge.runner.services.v1.data;


import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.CommandLine;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Definition;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@XmlType(name = "DefinitionData")
public class DefinitionData {
    private String identifier;
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "export", value = "Must be unique")
    private String title;
    @Valid
    @NotEmpty
    private List<CommandLineData> commands;
    @ApiModelProperty(value = "Max times to all command be run after it will be failed")
    private Integer maxTryCount;
    @ApiModelProperty(value = "Max milliseconds to all command be run after it will be failed")
    private Long maxTryDuration;

    public Definition fill(Definition def) {
        if(def == null) def = new Definition();
        def.setTitle(title);
        def.setCommands(new ArrayList<>());
        for (CommandLineData c : commands)
            def.getCommands().add(c.fill(null));
        def.setMaxTryCount(maxTryCount);
        def.setMaxTryDuration(maxTryDuration);
        return def;
    }

    public DefinitionData sync(Definition def) {
        identifier = def.getIdentifier();
        title = def.getTitle();
        commands = new ArrayList<>();
        if(def.getCommands() != null)
            for(CommandLine c: def.getCommands())
                commands.add(new CommandLineData().sync(c));
        maxTryCount = def.getMaxTryCount();
        maxTryDuration = def.getMaxTryDuration();
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

    public List<CommandLineData> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandLineData> commands) {
        this.commands = commands;
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
}
