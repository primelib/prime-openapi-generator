package io.github.primelib.primecodegen.core.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.List;

@UtilityClass
public class GeneratorUtils {

    /**
     * Creates the output directories if they don't exist.
     *
     * @param directories A list of directories to create.
     */
    public void createOutputDirectories(List<String> directories) {
        for (String directory : directories) {
            File directoryFile = new File(directory);
            if (!directoryFile.exists()) {
                if (!directoryFile.mkdirs()) {
                    throw new RuntimeException("Failed to create output directory: " + directory);
                }
            }
        }
    }

}
