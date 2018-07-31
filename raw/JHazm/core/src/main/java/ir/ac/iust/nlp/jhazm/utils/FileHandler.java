package ir.ac.iust.nlp.jhazm.utils;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Majid Asgari
 * Simple Utils for copying files in jar to the working path
 */
public class FileHandler {

    private static String copyRoot = ".";

    public static void setCopyRoot(String copyRoot) {
        FileHandler.copyRoot = copyRoot;
    }

    public static Path getPath(String name) throws IOException {
        if (!prepareFile(null, name)) return null;
        return Paths.get(copyRoot, name);
    }

    public static Path getPath(String folder, String name) throws IOException {
        if (!prepareFile(folder, name)) return null;
        return Paths.get(copyRoot, folder, name);
    }

    public static boolean prepareFile(String folder, String... names) {
        for (String name : names) {
            final Path file;
            if (folder == null) file = Paths.get(copyRoot, name);
            else file = Paths.get(copyRoot, folder, name);
            if (Files.exists(file)) return true;
            InputStream libraryInputStream = FileHandler.class.getResourceAsStream("/"
                    + (folder != null ? folder + "/" : "") + name);
            if (libraryInputStream == null)
                libraryInputStream = FileHandler.class.getResourceAsStream("/"
                        + (folder != null ? folder + "/" : "")
                        + name.substring(name.indexOf("/") + 1));
            try {
                if (file.getParent() != null && !Files.exists(file.getParent()))
                    Files.createDirectories(file.getParent());
                FileUtils.copyInputStreamToFile(libraryInputStream, file.toFile());
            } catch (IOException e) {
                e.printStackTrace();
                Logger.getAnonymousLogger().log(Level.INFO, "error occurred", e);
                return false;
            }
        }
        return true;
    }
}
