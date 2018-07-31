package ir.ac.iust.dml.kg.knowledge.runner.logic;

import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IDefinitionDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IHistoryDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IRunDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Main class that run definition and store data in db
 */
@Service
public class Manager implements RunnerListener {
    static final Logger LOGGER = LogManager.getLogger(Manager.class);
    private final IRunDao runs;
    private final IHistoryDao histories;
    private final IDefinitionDao definitions;
    private final Map<String, Runner> allRunning = new ConcurrentHashMap<>();
    private boolean running = true;

    @Autowired
    public Manager(IRunDao runs, IHistoryDao histories, IDefinitionDao definitions) {
        this.runs = runs;
        this.histories = histories;
        this.definitions = definitions;
    }


    @PostConstruct
    void setup() {
        runs.readAllNeedForRerun().forEach(this::run);
    }

    public Run run(String title) {
        final Definition def = definitions.readByTitle(title);
        if (def == null) return null;
        final Run run = new Run(def);
        runs.write(run);
        run(run);
        return run;
    }

    private void run(Run run) {
        if (!running) return;
        synchronized (allRunning) {
            if (allRunning.containsKey(run.getIdentifier())) return;
            final Runner runner = new Runner(run, this);
            allRunning.put(run.getIdentifier(), runner);
            runner.start();
        }
    }

    public void shutdown() {
        running = false;
        LOGGER.info("Shutdown all");
        allRunning.values().forEach(Runner::shutdown);
        allRunning.values().forEach(i -> {
            try {
                i.join();
            } catch (Exception ignored) {
            }
        });
    }

    public List<Run> getAllRunning() {
        synchronized (allRunning) {
            final List<Run> runs = new ArrayList<>();
            allRunning.values().forEach(r -> runs.add(r.getRun()));
            return runs;
        }
    }


    @Override
    public RunHistory open(Run run) throws Exception {
        return new RunHistoryWrapper(run, histories.create(run));
    }

    @Override
    public void started(Run run) {
        LOGGER.info(String.format("%s started", run));
        run.setStartEpoch(System.currentTimeMillis());
        run.setState(RunState.Running);
        if (run.getRemindedTryCount() != null)
            run.setRemindedTryCount(run.getRemindedTryCount() - 1);
        runs.write(run);
    }

    @Override
    public void completed(Run r, RunState state, Exception ex) {
        LOGGER.info(String.format("%s completed with %s", r, state));
        r.setEndEpoch(System.currentTimeMillis());
        r.setState(state);
        runs.write(r);
        synchronized (allRunning) {
            allRunning.remove(r.getIdentifier());
        }
        if(state == RunState.Succeed) return;
        if (r.getValidUntilEpoch() != null && r.getValidUntilEpoch() > System.currentTimeMillis()) return;
        if (r.getRemindedTryCount() != null && r.getRemindedTryCount() <= 0) return;
        run(r);
    }

    @Override
    public void reportProgress(Run run, float progress) {
        if (run.getProgress() == null || Math.abs(run.getProgress() - progress) >= 1) {
            LOGGER.info(String.format("Progress %s", progress));
            run.setProgress(progress);
            runs.write(run);
        } else
            run.setProgress(progress);
    }

    @Override
    public void command(Run run, CommandLine step, RunState result) {
        LOGGER.info(String.format("%s of %s completed with %s", step, run, result));
        step.setResult(result);
        runs.write(run);
    }
}
