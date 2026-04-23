package exceptions;

public class ActionExecutionFailedException extends RuntimeException {
    public ActionExecutionFailedException(String message) {
        super(message);
    }
}
