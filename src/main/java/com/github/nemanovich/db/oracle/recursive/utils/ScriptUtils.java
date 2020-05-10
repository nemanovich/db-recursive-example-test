package com.github.nemanovich.db.oracle.recursive.utils;

import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.testcontainers.shaded.org.apache.commons.lang.StringUtils.removeStart;


public class ScriptUtils {

    public static String loadScript(String resourceName) {
        try {
            return FileUtils.readFileToString(
                    new File(ScriptUtils.class.getResource("/db/" + removeStart(resourceName, "/")).getFile()),
                    "UTF-8"
            );
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error in reading resource " + resourceName, e);
        }
    }
}
