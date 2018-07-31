package ir.ac.iust.dml.kg.knowledge.runner.access.entities;

import ir.ac.iust.dml.kg.knowledge.runner.access.HistoryIOException;

import java.io.Closeable;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * History of output of process
 */
public abstract class RunHistory implements Closeable {
    protected final List<String> outputLines;
    protected final List<String> errorLines;

    public RunHistory(List<String> outputLines, List<String> errorLines) {
        this.outputLines = outputLines;
        this.errorLines = errorLines;
    }

    public List<String> getOutputLines() {
        return outputLines;
    }

    public List<String> getErrorLines() {
        return errorLines;
    }

    public abstract void appendError(String error) throws HistoryIOException;

    public abstract void appendOutput(String output) throws HistoryIOException;
}
