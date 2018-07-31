package ir.ac.iust.dml.kg.knowledge.runner.logic;


import ir.ac.iust.dml.kg.knowledge.runner.access.HistoryIOException;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.CommandLine;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunHistory;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Run a {@link Run} and read its output and errors
 */
public class Runner extends Thread {
    private final String PROGRESS_START = "#progress";
    private final Run run;
    private final RunnerListener listener;
    private boolean running;
    private Process current;

    public Runner(Run run, RunnerListener listener) {
        this.run = run;
        this.listener = listener;
        this.running = true;
    }

    @Override
    public void run() {
        try (RunHistory hist = listener.open(run)) {
            listener.started(run);
            for (CommandLine command : run.getCommands())
                if (running) {
                    final ProcessBuilder builder = new ProcessBuilder();
                    builder.command(command.commands());
                    if (command.getEnvironment() != null)
                        for (Map.Entry<String, String> entry : command.getEnvironment().entrySet())
                            command.getEnvironment().put(entry.getKey(), entry.getValue());
                    if (command.getWorkingDirectory() != null)
                        builder.directory(Paths.get(command.getWorkingDirectory()).toFile());
                    builder.redirectErrorStream(true);
                    current = builder.start();
                    final Thread thr1 = readOutput(current, hist);
                    final Thread thr2 = readError(current, hist);
                    thr1.join();
                    thr2.join();
                    if (current.exitValue() != 0) {
                        listener.command(run, command, RunState.Failed);
                        if (!command.isContinueOnFail()) {
                            listener.completed(run, RunState.Failed, null);
                            return;
                        }
                    } else
                        listener.command(run, command, RunState.Succeed);
                }

        } catch (HistoryIOException ex) {
            listener.completed(run, RunState.HistoryUnavailable, ex);
            return;
        } catch (Exception ex) {
            listener.completed(run, RunState.Failed, ex);
            return;
        }
        listener.completed(run, RunState.Succeed, null);
    }

    private Thread readOutput(Process process, RunHistory history) {
        final Thread thr = new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith(PROGRESS_START))
                        try {
                            final String str = line.substring(PROGRESS_START.length()).trim();
                            final float progress = Float.parseFloat(str);
                            listener.reportProgress(run, progress);
                        } catch (Throwable ignored) {

                        }
                    else
                        history.appendOutput(line);
                }
            } catch (Exception ignored) {
            }
        });
        thr.start();
        return thr;
    }

    private Thread readError(Process process, RunHistory history) {
        final Thread thr = new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    history.appendError(line);
                }
            } catch (Exception ignored) {
            }
        });
        thr.start();
        return thr;
    }

    void shutdown() {
        running = false;
        current.destroy();
    }

    public Run getRun() {
        return run;
    }
}
