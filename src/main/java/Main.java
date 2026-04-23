import actions.ActionSystem;
import input.ConsoleInputProvider;
import input.InputProvider;
import llm.LLMEngine;
import util.Logger;

public class Main {
    private final ActionSystem actionSystem = new ActionSystem();
    private final LLMEngine engine = new LLMEngine();

    void main() {
        Logger.setDebugMode(true);

        InputProvider inputProvider = new ConsoleInputProvider();
        inputProvider.startInput(this::processInput);
    }

    private void processInput(String input) {
        var time = System.currentTimeMillis();

        String commandName = engine.findIntent(input);
        if (commandName == null) {
            Logger.warn("No command");
            return;
        }
        Logger.debug(String.format("Found command: %s, for input: %s", commandName, input));

        var params = engine.extractParams(input, commandName);
        actionSystem.execute(commandName, params);

        var allTime = System.currentTimeMillis() - time;
        Logger.debug(allTime + "ms");
    }
}
