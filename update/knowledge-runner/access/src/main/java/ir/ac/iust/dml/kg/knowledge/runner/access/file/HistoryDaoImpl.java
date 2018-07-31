package ir.ac.iust.dml.kg.knowledge.runner.access.file;

import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IHistoryDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * All history saved in folder of user.home
 */
@Repository
@PropertySource("classpath:persistence.properties")
public class HistoryDaoImpl implements IHistoryDao {
    @Value("${history.location}")
    private String baseFile;

    @PostConstruct
    void setup() throws IOException {
        if (baseFile.startsWith("~"))
            baseFile = System.getProperty("user.home") + baseFile.substring(1);
        final Path dir = Paths.get(baseFile);
        if (!Files.exists(dir) && !Files.isDirectory(dir))
            Files.createDirectories(dir);
    }

    @Override
    public RunHistory create(Run run) throws Exception {
        final Path path1 = Paths.get(baseFile, run.getIdentifier() + ".output");
        final Path path2 = Paths.get(baseFile, run.getIdentifier() + ".error");
        return new RunHistoryImpl(path1, path2);
    }

    @Override
    public RunHistory read(Run run) throws IOException {
        final Path path1 = Paths.get(baseFile, run.getIdentifier() + ".output");
        final Path path2 = Paths.get(baseFile, run.getIdentifier() + ".error");
        return new RunHistoryImpl(Files.readAllLines(path1), Files.readAllLines(path2));
    }

    @Override
    public void delete(Run run) throws IOException {
        Files.deleteIfExists(Paths.get(baseFile, run.getIdentifier() + ".output"));
        Files.deleteIfExists(Paths.get(baseFile, run.getIdentifier() + ".error"));
    }
}
