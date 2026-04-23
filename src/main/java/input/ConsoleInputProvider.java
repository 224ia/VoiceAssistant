package input;

import java.util.Scanner;

public class ConsoleInputProvider implements InputProvider {
    private final Scanner sc = new Scanner(System.in);

    @Override
    public String input() {
        return sc.nextLine();
    }
}
