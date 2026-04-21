package IntentEnigine;

import java.util.Map;

public interface IntentEngine {
    String findIntent(String text);
    Map<String, String> extractParams(String text, String intent);
}
