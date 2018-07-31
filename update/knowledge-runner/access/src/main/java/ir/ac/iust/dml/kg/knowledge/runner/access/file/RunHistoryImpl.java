package ir.ac.iust.dml.kg.knowledge.runner.access.file;

import ir.ac.iust.dml.kg.knowledge.runner.access.HistoryIOException;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunHistory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Each history saved as one file
 */
public class RunHistoryImpl extends RunHistory {
    private final BufferedWriter pwo;
    private final BufferedWriter pwe;

    RunHistoryImpl(Path pathOutput, Path pathErrors) throws HistoryIOException {
        super(new ArrayList<>(), new ArrayList<>());
        try {
            pwo = Files.newBufferedWriter(pathOutput, Charset.forName("UTF-8"),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            pwe = Files.newBufferedWriter(pathErrors, Charset.forName("UTF-8"),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new HistoryIOException(e);
        }


    }

    RunHistoryImpl(List<String> outputLines, List<String> errorLines) {
        super(outputLines, errorLines);
        pwo = null;
        pwe = null;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void appendError(String error) throws HistoryIOException {
        try {
            pwe.write(error);
            pwe.newLine();
            errorLines.add(error);
        } catch (IOException e) {
            throw new HistoryIOException(e);
        }

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void appendOutput(String output) throws HistoryIOException {
        try {
            pwo.write(output);
            pwo.newLine();
            outputLines.add(output);
        } catch (IOException e) {
            throw new HistoryIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (pwo != null)
            pwo.close();
        if (pwe != null)
            pwe.close();
    }
}
