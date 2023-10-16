package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class NotLoggedInException extends Exception {
    public NotLoggedInException(String message) {
        super(message);
    }

    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}
