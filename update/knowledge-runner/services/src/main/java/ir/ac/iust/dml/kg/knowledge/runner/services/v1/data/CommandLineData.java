package ir.ac.iust.dml.kg.knowledge.runner.services.v1.data;

import io.swagger.annotations.ApiModelProperty;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.CommandLine;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Data of a command line definition
 */
public class CommandLineData {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, example = "java", value = "command to be run")
    private String command;
    @ApiModelProperty(value = "list of arguments")
    private String[] arguments;
    @ApiModelProperty(value = "Environment variable that need in format of a map")
    private Map<String, String> environment;
    @ApiModelProperty(value = "If command failed continue other command")
    private boolean continueOnFail;
    @ApiModelProperty(value = "Working directory of command")
    private String workingDirectory;

    public CommandLine fill(CommandLine cmd) {
        if(cmd == null) cmd = new CommandLine();
        cmd.setCommand(command);
        cmd.setArguments(arguments);
        cmd.setEnvironment(environment);
        cmd.setContinueOnFail(continueOnFail);
        cmd.setWorkingDirectory(workingDirectory);
        return cmd;
    }

    public CommandLineData sync(CommandLine cmd) {
        command = cmd.getCommand();
        arguments = cmd.getArguments();
        environment = cmd.getEnvironment();
        continueOnFail = cmd.isContinueOnFail();
        workingDirectory = cmd.getWorkingDirectory();
        return this;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public boolean isContinueOnFail() {
        return continueOnFail;
    }

    public void setContinueOnFail(boolean continueOnFail) {
        this.continueOnFail = continueOnFail;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
