package Actions;

import java.util.HashMap;
import java.util.Map;

public class ActionSystem {
    private final Map<String, Action> actions = new HashMap<>();

    public void register(String name, Action action) {
        actions.put(name, action);
    }

    public void execute(String name, Map<String, String> params) {
        if (actions.containsKey(name)) {
            actions.get(name).execute(params);
        } else {
            System.out.println("Unknown action: " + name);
        }
    }
}
