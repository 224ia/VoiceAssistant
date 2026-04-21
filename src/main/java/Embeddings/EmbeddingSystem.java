package Embeddings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class EmbeddingSystem {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int VECTOR_SIZE = 384;

    public static float[] embed(String text) {
        return embed(new String[]{text})[0];
    }

    public static float[][] embed(String... texts) {
        try {
            String json = getJson(texts);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/embed"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error: " + response.statusCode());
            }

            return parseMultipleEmbeddings(response.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static float[][] parseMultipleEmbeddings(String json) throws IOException {
        Map<String, List<List<Double>>> map = mapper.readValue(
                json,
                new TypeReference<>() {}
        );

        List<List<Double>> embeddingsList = map.get("embeddings");
        float[][] result = new float[embeddingsList.size()][VECTOR_SIZE];

        for (int i = 0; i < embeddingsList.size(); i++) {
            List<Double> vector = embeddingsList.get(i);
            float[] floatVector = result[i];
            for (int j = 0; j < VECTOR_SIZE && j < vector.size(); j++) {
                floatVector[j] = vector.get(j).floatValue();
            }
        }

        return result;
    }

    private static String getJson(String... texts) {
        StringBuilder json = new StringBuilder("{\"texts\": [");
        for (int i = 0; i < texts.length; i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(texts[i])).append("\"");
        }
        json.append("]}");
        return json.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}