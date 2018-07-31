package ir.ac.iust.dml.kg.knowledge.runner.logic;

import ir.ac.iust.dml.kg.knowledge.runner.access.entities.CommandLine;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunHistory;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Event list that {@link Runner} send to {@link Manager}
 */
public interface RunnerListener {
    RunHistory open(Run run) throws Exception;

    void started(Run run);

    void completed(Run run, RunState state, Exception ex);

    void reportProgress(Run run, float progress);

    void command(Run run, CommandLine step, RunState failed);
}
