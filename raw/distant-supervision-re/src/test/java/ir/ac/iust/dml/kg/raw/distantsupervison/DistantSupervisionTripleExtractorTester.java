package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleBuilder;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExporter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by hemmatan on 9/6/2017.
 */
public class DistantSupervisionTripleExtractorTester {
    @org.junit.Test
    public void writeRawTriple() throws IOException {
        final String MODULE = Configuration.moduleName;
        final String URL = "mongo://Corpus/";
        final long TIME = System.currentTimeMillis();
        final String VERSION = "2.3";
        final RawTripleBuilder builder = new RawTripleBuilder(MODULE, URL, TIME, VERSION);
        DistantSupervisionTripleExtractor distantSupervisionTripleExtractor = new DistantSupervisionTripleExtractor();
        List<RawTriple> triples = distantSupervisionTripleExtractor.extract(URL, VERSION, "Sample Text");

        final Path PATH = Paths.get(System.getProperty("user.home"), "distantSupervisionTest.json");
        final RawTripleExporter exporter = new RawTripleExporter(PATH);
        for (RawTriple rawTriple :
                triples) {
            exporter.write(rawTriple);
        }
        exporter.close();
    }
}
