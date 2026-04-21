package IntentEnigine;

import Embeddings.EmbeddingSystem;
import Intent.Intent;
import Util.Logger;

import java.util.List;
import java.util.Map;

public class EmbeddingEngine implements IntentEngine {
    private List<Intent> intents;

    EmbeddingEngine(List<Intent> intents) {
        this.intents = intents;
    }

    @Override
    public String findIntent(String text) {
        float[] targetEmbedding = EmbeddingSystem.embed(text);
        float bestScore = -1;
        String winner = null;

        for (Intent intent : intents) {
            for (float[] example : intent.examples) {
                float score = cosineSimilarity(targetEmbedding, example);
                if (score > bestScore) {
                    bestScore = score;
                    winner = intent.name;
                }
            }
        }

        Logger.debug(String.format("Winner: %s with score %.4f", winner, bestScore));
        return winner;
    }

    @Override
    public Map<String, String> extractParams(String text, String intent) {
        return Map.of();
    }

    public float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same length");
        }

        float dotProduct = 0;
        float normA = 0;
        float normB = 0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
