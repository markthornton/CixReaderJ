package uk.me.mthornton.secrets;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestSecrets {
    private static final String CONTEXT = "TEST";
    
    @Test public void loadSecrets() {
        Secrets secrets = Secrets.getSecrets(CONTEXT);
        assertNotNull(secrets);
    }

    @Test public void missingSecret() {
        Secrets secrets = Secrets.getSecrets(CONTEXT);
        assertThrows(NoSuchElementException.class, () -> secrets.getSecret("missing"));
    }

    @Test public void addSecret() {
        addSecret("adolf", "hitler");
    }

    @Test public void addSecretPhrase() {
        addSecret("phrase", "The quick brown fox jumped over the lazy dog");
    }

    private void addSecret(String key, String value) {
        Secrets secrets = Secrets.getSecrets(CONTEXT);
        try {
            assertEquals(value, secrets.getSecretString(key));
            System.out.println("Matched existing value for "+key);
        } catch (NoSuchElementException e) {
            // value not yet present
        }
        secrets.setSecret(key, value);
        assertEquals(value, secrets.getSecretString(key));
    }
}
