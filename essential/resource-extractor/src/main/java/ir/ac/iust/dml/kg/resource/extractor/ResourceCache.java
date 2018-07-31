package ir.ac.iust.dml.kg.resource.extractor;


import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Cache iReaders to a directory to read it again
 * Directory must be empty
 */
public class ResourceCache implements IResourceReader {
    private final static Logger LOGGER = Logger.getLogger(ResourceCache.class);
    private final Path path;
    private final boolean useFSTCache;
    private int writeIndex = 0;
    private int readIndex = 0;
    private final ExecutorService readExecutor = Executors.newFixedThreadPool(8);
    private final List<Future<List<Resource>>> ingoingReading = new ArrayList<>();

    public ResourceCache(String path, boolean useFSTCache) throws IOException {
        this.path = Paths.get(path);
        this.useFSTCache = useFSTCache;
    }

    public ResourceCache(String path) throws IOException {
        this(path, false);
    }

    /**
     * Clear cache directory
     *
     * @throws IOException
     */
    public void Clear() throws IOException {
        if (!Files.exists(path))
            Files.createDirectories(path);
        else if (!Files.isDirectory(path))
            throw new RuntimeException("path must be a director");
        else {
            FileUtils.deleteDirectory(path.toFile());
            Files.createDirectories(path);
        }
        writeIndex = readIndex = 0;
        LOGGER.info("Cache directory is: " + path.toAbsolutePath().toString());
    }

    public void cache(IResourceReader reader, int pageSize) throws Exception {
        while (!reader.isFinished()) {
            final List<Resource> resources = reader.read(pageSize);
            if (resources.isEmpty()) continue;
            // write object to file
            try (OutputStream fos = Files.newOutputStream(getNextWritePath())) {
                try (ObjectOutput oos = useFSTCache ? new FSTObjectOutput(fos) : new ObjectOutputStream(fos)) {
                    oos.writeObject(resources);
                    oos.close();
                }
            }
        }
        LOGGER.info("Cache reader index" + writeIndex);
    }

    private synchronized Path getNextWritePath() {
        return path.resolve(String.format("%06d", writeIndex++));
    }

    private synchronized Path getNextReadPath() {
        return path.resolve(String.format("%06d", readIndex++));
    }

    private synchronized Path getCurrentReadPath() {
        return path.resolve(String.format("%06d", readIndex));
    }

    @Override
    public List<Resource> read(int pageSize) throws Exception {
        if (readIndex == 0) //Start to queue all request
            while (true) {
                final Path path = getNextReadPath();
                if (!Files.exists(path)) break;
                ingoingReading.add(readExecutor.submit(() -> {
                    try (InputStream in = Files.newInputStream(path)) {
                        try (ObjectInput ois = useFSTCache ? new FSTObjectInput(in) : new ObjectInputStream(in)) {
                            return (List<Resource>) ois.readObject();
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                    return null;
                }));
            }
        if (ingoingReading.size() > 0) {
            final Future<List<Resource>> first = ingoingReading.get(0);
            if (first.isDone()) {
                ingoingReading.remove(0);
                return first.get();
            } else
                Thread.sleep(10);
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean isFinished() {
        return !Files.exists(getCurrentReadPath()) && ingoingReading.isEmpty();
    }

    @Override
    public void close() throws Exception {
        readExecutor.shutdown();
    }
}
