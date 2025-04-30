package customexception;

public class InvalidGenderException extends Exception {
    public InvalidGenderException(String message) {
        super(message);
    }
}
