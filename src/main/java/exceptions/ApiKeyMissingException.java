package exceptions;

public class ApiKeyMissingException extends ConfigurationException {
    public ApiKeyMissingException(String message) {
        super(message);
    }
}
