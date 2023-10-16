package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class AlreadyLoggedException extends Exception {
    public AlreadyLoggedException(String message) {
        super(message);
    }

    public AlreadyLoggedException(String message, Throwable cause) {
        super(message, cause);
    }
}
