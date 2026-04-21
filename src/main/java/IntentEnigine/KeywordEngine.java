package IntentEnigine;

import Intent.Intent;

import java.util.List;
import java.util.Map;

public class KeywordEngine implements IntentEngine {
    List<Intent> intents;

    KeywordEngine(List<Intent> intents) {
        this.intents = intents;
    }

    public String findIntent(String text) {
        text = text.toLowerCase();

//        for (Intent.Intent intent : intents) {
//            for (String example : intent.examples) {
//                if (text.contains(example)) {
//                    return intent.name;
//                }
//            }
//        }
        return null;
    }

    @Override
    public Map<String, String> extractParams(String text, String intent) {
        return Map.of();
    }
}
