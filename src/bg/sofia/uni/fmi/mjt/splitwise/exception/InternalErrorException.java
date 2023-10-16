package bg.sofia.uni.fmi.mjt.splitwise.exception;

import bg.sofia.uni.fmi.mjt.splitwise.logger.Logger;

import java.time.LocalDateTime;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(String message, Throwable cause) {
        super(message, cause);
        Logger.log(LocalDateTime.now(), cause.getMessage());
    }

    public InternalErrorException(Throwable cause) {
        super(cause);
        Logger.log(LocalDateTime.now(), cause.getMessage());
    }
}
