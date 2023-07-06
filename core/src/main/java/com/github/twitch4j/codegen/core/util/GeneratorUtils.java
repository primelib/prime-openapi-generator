package com.github.twitch4j.codegen.core.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.Set;

@UtilityClass
public class GeneratorUtils {

    public void createOutputDirectories(Set<String> directories) {
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
