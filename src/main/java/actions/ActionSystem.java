package actions;

import exceptions.AnnotationMissingException;
import util.Logger;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActionSystem {
    private final Map<String, Action> actions = new HashMap<>();

    private static final String CUSTOM_ACTIONS_PACKAGE_PATH = "actions.custom";

    public ActionSystem() {
        Logger.info("actions system was created");
        registerAnnotated();
    }

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

    public void registerAnnotated() {
        Reflections reflections = new Reflections(CUSTOM_ACTIONS_PACKAGE_PATH);
        Set<Class<? extends Action>> actionClasses =
                reflections.getSubTypesOf(Action.class);

        for (Class<? extends Action> clazz : actionClasses) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                Logger.debug("Skipping abstract class: " + clazz.getName());
                continue;
            }

            CommandAction annotation = clazz.getAnnotation(CommandAction.class);
            if (annotation == null) {
                Logger.error("Missing @CommandAction: " + clazz.getName());
                throw new AnnotationMissingException("Missing @CommandAction: " + clazz.getName());
            }

            String command = annotation.command();
            try {
                Action action = clazz.getDeclaredConstructor().newInstance();
                register(command, action);
                Logger.info("Registered: " + command + " → " + clazz.getSimpleName());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                Logger.error("Failed to register: " + command, e);
            }
        }
    }
}
