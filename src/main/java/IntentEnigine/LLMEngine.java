package IntentEnigine;

import ConfigParser.CommandSchemaParser;
import ConfigParser.Configuration;
import Util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LLMEngine implements IntentEngine {
    private static final Path COMMANDS_PATH = Path.of("src/main/resources/commands.json");
    private static final CommandSchemaParser commandParser;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String API_KEY = Configuration.getRequired("GROQ_API_KEY");
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final String REQUEST_JSON = """
        {
            "model": "llama-3.3-70b-versatile",
            "messages": [
                {"role": "system", "content": "Ти асистент. Відповідай коротко та швидко."},
                {"role": "user", "content": "%s"}
            ]
        }
        """;

    private static final String CHOOSE_COMMAND;
    private static final String FILL_COMMAND = "Заповни команду (тільки JSON формат за схемою, жодних коментарів чи нових слів, тільки заповнену схему, без оригінальної): %s від цього запиту: %s";

    static {
        try {
            String commandsJson = Files.readString(COMMANDS_PATH);
            commandParser = new CommandSchemaParser(commandsJson);
            String commandList = commandParser.getCommandsList();
            CHOOSE_COMMAND = String.format("Напиши назву команди для цього запиту." +
                    " Доступні команди: %s. Запит: ", commandList);
            Logger.info("Loaded commands: " + commandList);
        } catch (IOException e) {
            Logger.error("Failed to parse commands", e);
            throw new RuntimeException();
        }
    }

    @Override
    public String findIntent(String text) {
        String commandName = chat(CHOOSE_COMMAND + text);
        if (commandName == null) {
            Logger.warn("No command found");
        }
        return commandName;
    }

    @Override
    public Map<String, String> extractParams(String text, String intent) {
        String paramsJson = chat(FILL_COMMAND.formatted(commandParser.getParamsSchema(intent), text));

        if (paramsJson == null || paramsJson.isBlank()) {
            return Map.of();
        }

        try {
            Map<String, Object> raw = mapper.readValue(paramsJson, new TypeReference<>() {});

            Map<String, String> result = new HashMap<>();
            for (var entry : raw.entrySet()) {
                if (entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue().toString());
                }
            }
            return result;
        } catch (JsonProcessingException e) {
            Logger.error("Failed to parse: " + paramsJson, e);
            return Map.of();
        }
    }

    public String chat(String userMessage) {
        Logger.debug(userMessage);

        if (API_KEY.isBlank()) {
            Logger.error("API_KEY відсутній!");
            return null;
        }

        String json = REQUEST_JSON.formatted(escapeJson(userMessage));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println(response.body());
                return parseResponse(response.body());
            } else {
                Logger.error("Error: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            Logger.error("Failed to connect to the server", e);
            return null;
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String parseResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (JsonProcessingException e) {
            Logger.error("Failed to parse response", e);
            return null;
        }
    }
}
