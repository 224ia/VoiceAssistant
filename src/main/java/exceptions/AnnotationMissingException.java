package exceptions;

public class AnnotationMissingException extends RuntimeException {
    public AnnotationMissingException(String message) {
        super(message);
    }
}
