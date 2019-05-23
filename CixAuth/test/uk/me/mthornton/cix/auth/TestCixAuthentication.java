package uk.me.mthornton.cix.auth;

import org.junit.jupiter.api.Test;
import uk.me.mthornton.utility.ApplicationConfiguration;

import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutionException;

public class TestCixAuthentication {

    @Test public void getToken() throws ExecutionException, InterruptedException {
        ApplicationConfiguration configuration = new ApplicationConfiguration(ClientCredentials.getApplicationId());
        String user = configuration.get(ClientCredentials.getUserProperty(), String.class);
        CixAuthentication auth = new CixAuthentication(HttpClient.newHttpClient());
        String token = auth.getAuthenticationToken(user).toCompletableFuture().get();
        System.out.println(token);
        assertNotNull(token);
        token = auth.getAuthenticationToken(user).toCompletableFuture().get();
        assertNotNull(token);
    }
}
