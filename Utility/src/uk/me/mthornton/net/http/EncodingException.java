package uk.me.mthornton.net.http;

public class EncodingException extends RuntimeException {
    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncodingException(Throwable cause) {
        super(cause);
    }
}
