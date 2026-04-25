package config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ConfigurationException;
import util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandLoader {
    private static final Path COMMANDS_DIR = Path.of("src/main/resources/commands/");
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, CommandDefinition> commands = new HashMap<>();

    public CommandLoader() {
        loadAllCommands();
    }

    private void loadAllCommands() {
        try (Stream<Path> stream = Files.list(COMMANDS_DIR)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(file -> {
                        try {
                            String fileName = file.getFileName().toString();
                            String commandName = fileName.substring(0, fileName.lastIndexOf('.'));

                            CommandDefinition cmd = mapper.readValue(file.toFile(), CommandDefinition.class);
                            if (commandName.equals("change_app")) {
                                cmd.params.get("app_name").enumValues = ProgramPaths.getKeys();
                            }
                            commands.put(commandName, cmd);

                            Logger.info("Loaded command: " + commandName);
                        } catch (IOException e) {
                            Logger.error("Failed to load: " + file, e);
                        }
                    });
        } catch (IOException e) {
            Logger.error("Failed to read commands directory: " + COMMANDS_DIR, e);
            throw new ConfigurationException("Failed to read commands directory: " + COMMANDS_DIR);
        }
    }

    public String getCommandsList() {
        return commands.entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue().getDescription() + ")")
                .collect(Collectors.joining(", "));
    }

    public String getParamsSchema(String commandName) {
        CommandDefinition cmd = commands.get(commandName);
        if (cmd == null) return null;

        try {
            return mapper.writeValueAsString(cmd.getParams());
        } catch (Exception e) {
            return null;
        }
    }

    public CommandDefinition getCommand(String name) {
        return commands.get(name);
    }

    public static class CommandDefinition {
        private String description;
        private Map<String, ParamDefinition> params;

        public String getDescription() { return description; }
        public Map<String, ParamDefinition> getParams() { return params; }
    }

    public static class ParamDefinition {
        private String type;
        @JsonProperty("enum")
        private List<String> enumValues;
        private boolean required;
        private Map<String, ParamDefinition> properties;
        private Double min;
        private Double max;

        public String getType() { return type; }
        public List<String> getEnumValues() { return enumValues; }
        public boolean isRequired() { return required; }
        public Map<String, ParamDefinition> getProperties() { return properties; }
        public Double getMin() { return min; }
        public Double getMax() { return max; }
    }
}
