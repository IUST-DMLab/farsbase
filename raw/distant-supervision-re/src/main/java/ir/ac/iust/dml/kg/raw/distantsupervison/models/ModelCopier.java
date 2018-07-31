package ir.ac.iust.dml.kg.raw.distantsupervison.models;

import ir.ac.iust.dml.kg.raw.distantsupervison.Configuration;
import ir.ac.iust.dml.kg.raw.distantsupervison.SharedResources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModelCopier {

    final private static String[] modelFiles = new String[]{
            "allowedEntityTypes.txt",
            "entity.txt",
            "mappings.txt",
            "model",
            "objectFollowing.model",
            "objectPreceding.model",
            "posModel.txt",
            "predicates.txt",
            "predicatesToLoad.txt",
            "subjectFollowing.model",
            "subjectPreceding.model"
    };

    private static boolean prepared = false;

    public static void prepare() {
        if (prepared) return;
        try {
            final Path logitPath = Paths.get(SharedResources.logitDirectory);
            if (!Files.exists(logitPath)) Files.createDirectories(logitPath);
            for (final String classifierType : Configuration.classifierTypes) {
                final Path folder = logitPath.resolve(classifierType);
                if (!Files.exists(folder)) Files.createDirectories(folder);
                for (final String modelFile : modelFiles) {
                    try {
                        Files.copy(ModelCopier.class.getResourceAsStream("/logit/" + classifierType + "/" + modelFile),
                                folder.resolve(modelFile));
                    } catch (Throwable ignored) {
                        // we may haven't expected file in resources.
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepared = true;
    }
}
