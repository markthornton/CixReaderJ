package uk.me.mthornton.cix.auth;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutionException;

public class TestCixAuthentication {

    @Test public void getToken() throws ExecutionException, InterruptedException {
        CixAuthentication auth = new CixAuthentication(HttpClient.newHttpClient());
        String token = auth.getAuthenticationToken("mthorn").toCompletableFuture().get();
        System.out.println(token);
        assertNotNull(token);
        token = auth.getAuthenticationToken("mthorn").toCompletableFuture().get();
        assertNotNull(token);
    }
}
