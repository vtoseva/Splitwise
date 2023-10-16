package bg.sofia.uni.fmi.mjt.splitwise.exception;

// logger messages should be different from user ones
public class InvalidUsernameException extends Exception {
    public InvalidUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUsernameException(String message) {
        super(message);
    }
}
