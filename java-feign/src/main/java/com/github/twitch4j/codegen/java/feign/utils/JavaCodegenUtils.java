package com.github.twitch4j.codegen.java.feign.utils;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class JavaCodegenUtils {

    /**
     * packageToPath replaces all dots in the path with a /
     *
     * @param sourceFolder
     * @param packageName
     * @return
     */
    public String packageToPath(String outputFolder, String sourceFolder, String packageName) {
        return (outputFolder + File.separator + sourceFolder + File.separator + packageName).replace(".", File.separator);
    }

}
