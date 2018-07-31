package ir.ac.iust.dml.kg.knowledge.runner.access.entities;

import java.util.Arrays;
import java.util.Map;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Command Line that run as java process
 */
public class CommandLine {
    private String command;
    private String[] arguments;
    private Map<String, String> environment;
    private boolean continueOnFail;
    private String workingDirectory;
    private RunState result;

    public CommandLine() {
    }

    public CommandLine(String command, String... arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public CommandLine(String command, String[] arguments, Map<String, String> environment) {
        this.command = command;
        this.arguments = arguments;
        this.environment = environment;
    }

    public String[] commands() {
        if (arguments == null || arguments.length == 0)
            return new String[]{command};
        final String[] commands = new String[arguments.length + 1];
        commands[0] = command;
        System.arraycopy(arguments, 0, commands, 1, arguments.length);
        return commands;
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

    public RunState getResult() {
        return result;
    }

    public void setResult(RunState result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("Command(%s %s)", command, Arrays.toString(arguments));
    }
}
