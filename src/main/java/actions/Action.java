package actions;

import java.util.Map;

@FunctionalInterface
public interface Action {
    void execute(Map<String, String> params);
}
