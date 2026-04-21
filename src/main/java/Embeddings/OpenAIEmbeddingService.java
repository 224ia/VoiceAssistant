package Embeddings;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.embeddings.CreateEmbeddingResponse;
import com.openai.models.embeddings.EmbeddingCreateParams;
import com.openai.models.embeddings.EmbeddingModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenAIEmbeddingService {

    private final OpenAIClient client;

    public OpenAIEmbeddingService() {
        Path configPath = Path.of("ConfigParser.Configuration.txt");
        try {
            String config = Files.readString(configPath);
            String apiKey = config.split("=")[1].replaceAll("\"", "").trim();
            System.out.println(apiKey);
            this.client = OpenAIOkHttpClient.fromEnv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getEmbedding(String text) {
        EmbeddingCreateParams params = EmbeddingCreateParams.builder()
                .input(text)
                .model(EmbeddingModel.TEXT_EMBEDDING_3_SMALL)
                .build();
        CreateEmbeddingResponse createEmbeddingResponse = client.embeddings().create(params);

        System.out.println(createEmbeddingResponse.data().getFirst().embedding());

//        double[] result = new double[vector.size()];
//        for (int i = 0; i < vector.size(); i++) {
//            result[i] = vector.get(i);
//        }
//
//        return result;
    }
}