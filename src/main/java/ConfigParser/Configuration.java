package ConfigParser;

import Util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Configuration {
    private static final Properties props = new Properties();

    static {
        try {
            props.load(Files.newBufferedReader(Path.of("Configuration.txt")));
        } catch (IOException e) {
            Logger.error("Failed to load the configuration", e);
            throw new RuntimeException();
        }
    }

    public static String getRequired(String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            Logger.error("Missing key: " + key);
            throw new IllegalStateException();
        }
        return value;
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}