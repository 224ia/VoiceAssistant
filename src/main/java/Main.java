import Actions.ActionSystem;
import Actions.ChangeAppAction;
import Embeddings.EmbeddingSystem;
import Intent.Intent;
import IntentEnigine.IntentEngine;
import IntentEnigine.LLMEngine;
import Util.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static void main() {
        Logger.setDebugMode(true);

//        var intents = addBaseIntents();
//        var handlers = addBaseIntentHandlers();
        var actions = addActions();

        IntentEngine engine = new LLMEngine();
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        var sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            var time = System.currentTimeMillis();
            String intent = engine.findIntent(input);
            if (intent == null) {
                Logger.warn("No command");
                continue;
            }
            Logger.debug(intent);
            var params = engine.extractParams(input, intent);
            actions.execute(intent, params);
            var allTime = System.currentTimeMillis() - time;
            Logger.debug(allTime + "ms");
        }
    }

    private static ActionSystem addActions() {
        var actions = new ActionSystem();

        actions.register("change_app", new ChangeAppAction());

        return actions;
    }

    private static ArrayList<Intent> addBaseIntents() {
        var intents = new ArrayList<Intent>();

        intents.add(new Intent("open_browser",
                EmbeddingSystem.embed("відкрий браузер", "запусти інтернет", "хочу в інтернет", "дай посерфити мережу", "запусти хром", "відкрий фаєрфокс")));
        intents.add(new Intent("open_app",
                EmbeddingSystem.embed("відкрий програму", "запусти телеграм", "хочу зайти в дискорд")));
        intents.add(new Intent("change_light",
                EmbeddingSystem.embed("зміни світло", "зроби інший колір", "зміни колір світла")));

        return intents;
    }

//    private static Map<String, IntentHandler> addBaseIntentHandlers() {
//        var handlers = new HashMap<String, IntentHandler>();
//
//        Action openAppAction = params -> {
//            System.out.println("Opening app");
//            String path = params.get("path");
//            if (path == null) {
//                System.out.println("No path found");
//                return;
//            }
//            try {
//                Runtime.getRuntime().exec(path);
//            } catch (IOException e) {
//                System.err.println("Opening failed");
//            }
//        };
//
//        handlers.put("open_browser", new IntentHandler(new OpenBrowserParams(), openAppAction));
//        handlers.put("open_app", new IntentHandler(new OpenAppParams(), openAppAction));
//
//        return handlers;
//    }
}
