package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class DuplicateGroupNameException extends Exception {
    public DuplicateGroupNameException(String message) {
        super(message);
    }

    public DuplicateGroupNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
