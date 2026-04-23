package config;

import exceptions.ConfigurationException;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Configuration {
    private static final Properties props = new Properties();

    private static final Path CONFIGURATION_FILE_PATH = Path.of("src/main/resources/config/application.properties");

    static {
        try {
            props.load(Files.newBufferedReader(CONFIGURATION_FILE_PATH));
        } catch (IOException e) {
            Logger.error("Failed to load the configuration", e);
            throw new ConfigurationException("Configuration failed: no such file in path: " + CONFIGURATION_FILE_PATH, e);
        }
    }

    public static String getRequired(String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            Logger.error("Missing key: " + key);
            throw new ConfigurationException("Missing key:" + key + " in properties");
        }
        return value;
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}