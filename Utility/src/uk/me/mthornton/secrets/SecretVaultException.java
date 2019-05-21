package uk.me.mthornton.secrets;

public class SecretVaultException extends RuntimeException {
    public SecretVaultException(String message) {
        super(message);
    }

    public SecretVaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecretVaultException(Throwable cause) {
        super(cause);
    }
}
