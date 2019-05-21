package uk.me.mthornton.cix.auth;

public class CommunicationException extends RuntimeException {
    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }
}
