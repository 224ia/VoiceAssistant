package actions.custom;

import actions.Action;
import actions.CommandAction;
import util.Logger;

import java.io.IOException;
import java.util.Map;

@CommandAction(command = "turn_off_the_pc")
public class TurnOffThePcAction implements Action {
    private static final String SHUTDOWN_PATH = "shutdown -s -t 0";

    @Override
    public void execute(Map<String, String> params) {
        try {
            new ProcessBuilder(SHUTDOWN_PATH).start();
        } catch (IOException e) {
            Logger.error("Failed to turn the pc off", e);
        }
    }
}
