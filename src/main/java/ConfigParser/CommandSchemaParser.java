package ConfigParser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.*;

public class CommandSchemaParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, CommandDefinition> commands;

    public CommandSchemaParser(String json) throws IOException {
        this.commands = mapper.readValue(json,
                new TypeReference<Map<String, CommandDefinition>>() {});
    }

    public String getCommandsList() {
        return String.join(", ", commands.keySet());
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

    public Map<String, CommandDefinition> getAllCommands() {
        return commands;
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