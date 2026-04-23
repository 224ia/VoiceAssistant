package actions.custom;

import actions.Action;
import actions.CommandAction;
import exceptions.ActionExecutionFailedException;
import util.Logger;

import java.util.Map;

@CommandAction(command = "set_timer")
public class SetTimerAction implements Action {
    private static final Map<String, Integer> unitMultipliers = Map.of(
            "seconds", 1,
            "minutes", 60,
            "hours", 3600
    );

    @Override
    public void execute(Map<String, String> params) {
        String timeString = params.get("time");
        String unit = params.get("unit");

        if (timeString == null || unit == null) {
            Logger.warn("Missing time or unit parameter");
            throw new ActionExecutionFailedException("Не отримано час або одиницю виміру для таймера");
        }

        try {
            int time = Integer.parseInt(timeString);
            int seconds = convertToSeconds(time, unit);
            Logger.info("Setting timer on " + seconds + " seconds");
        } catch (NumberFormatException e) {
            Logger.warn("Time must be a number, got: " + timeString);
            throw new ActionExecutionFailedException("Отриманий час не є числом");
        }
    }

    private int convertToSeconds(int time, String unit) {
        Integer multiplier = unitMultipliers.get(unit.toLowerCase());
        if (multiplier == null) {
            Logger.warn("Unknown unit: " + unit + ", assuming seconds");
            return time;
        }
        return time * multiplier;
    }
}