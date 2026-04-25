package config;

import util.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ProgramPaths {
    private static final Map<String, String> appPaths;

    private static final Path APP_PATHS_FILE_PATH = Path.of("src/main/resources/app_paths.json");

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        try {
            String json = Files.readString(APP_PATHS_FILE_PATH);
            appPaths = mapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            Logger.error("Не вдалося завантажити конфігурацію", e);
            throw new RuntimeException();
        }
    }

    public static String get(String key) {
        return appPaths.get(key);
    }

    public static List<String> getKeys() {
        return appPaths.keySet().stream().toList();
    }
}
