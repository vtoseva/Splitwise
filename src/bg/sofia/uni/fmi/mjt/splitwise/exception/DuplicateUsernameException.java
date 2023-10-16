package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class DuplicateUsernameException extends Exception {
    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
