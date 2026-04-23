package input;

import java.util.function.Consumer;

public interface InputProvider {
    default void startInput(Consumer<String> inputProcessFunction) {
        while (true) {
            String input = input();
            inputProcessFunction.accept(input);
        }
    }

    String input();
}
