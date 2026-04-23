package actions.custom;

import actions.Action;
import actions.CommandAction;
import config.ProgramPaths;
import util.Logger;

import java.io.IOException;
import java.util.Map;

@CommandAction(command = "change_app")
public class ChangeAppAction implements Action {
    @Override
    public void execute(Map<String, String> params) {
        String appName = params.get("app_name");
        if (appName == null || appName.isBlank()) {
            Logger.warn("App name required");
        }
        String state = params.get("state");
        switch (state) {
            case "on" -> openApp(appName);
            case "off" -> closeApp(appName);
        }
    }

    private void openApp(String appName) {
        Logger.debug("Opening app: " + appName);
        String path = ProgramPaths.get(appName);
        if (path == null) {
            Logger.warn("No path found");
            return;
        }
        try {
            new ProcessBuilder(path).start();
        } catch (IOException e) {
            Logger.error("Failed to start the app", e);
        }
    }

    private void closeApp(String appName) {
        Logger.debug("Closing app: " + appName);
        try {
            new ProcessBuilder("taskkill", "/IM", appName + ".exe", "/F").start();
        } catch (IOException e) {
            Logger.error("Failed to close the app", e);
        }
    }
}
