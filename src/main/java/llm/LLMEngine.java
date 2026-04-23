package llm;

import config.CommandLoader;
import config.Configuration;
import exceptions.ApiKeyMissingException;
import util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class LLMEngine {
    private static final CommandLoader commandLoader = new CommandLoader();

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
    private static final String FILL_COMMAND = "Заповни команду (тільки JSON формат за схемою, жодних коментарів чи нових слів, тільки заповнену схему, в форматі \"змінна\": \"значення\" без її параметрів): %s від цього запиту: %s";

    static {
        String commandList = commandLoader.getCommandsList();
        CHOOSE_COMMAND = String.format("Напиши назву команди для цього запиту. Тільки назву команди, нічого більше. Шукай відповідну" +
                " Доступні команди: %s. Запит: ", commandList);
        Logger.info("Loaded commands: " + commandList);
    }

    public String findIntent(String text) {
        String commandName = chat(CHOOSE_COMMAND + text);
        if (commandName == null) {
            Logger.warn("No command found");
        }
        return commandName;
    }

    public Map<String, String> extractParams(String text, String intent) {
        String paramsSchema = commandLoader.getParamsSchema(intent);
        if (paramsSchema == null || "null".equals(paramsSchema)) {
            Logger.debug(String.format("Command: %s, for input: %s, hasn't params", intent, text));
            return Map.of("input", text);
        }
        Logger.debug(String.format("Parameter Schema for command: %s, - %s", intent, paramsSchema));
        String paramsJson = chat(FILL_COMMAND.formatted(paramsSchema, text));
        Logger.debug(paramsJson);

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

    public static String chat(String userMessage) {
        Logger.debug(userMessage);

        if (API_KEY.isBlank()) {
            Logger.error("API_KEY відсутній!");
            throw new ApiKeyMissingException("API key is missing, check resources/config/application.example.properties file");
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
                Logger.error("Error: " + response.body() + " with status code: " + response.statusCode());
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

    private static String parseResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (JsonProcessingException e) {
            Logger.error("Failed to parse response", e);
            return null;
        }
    }
}
