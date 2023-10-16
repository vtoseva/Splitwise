package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
