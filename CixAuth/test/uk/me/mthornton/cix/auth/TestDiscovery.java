package uk.me.mthornton.cix.auth;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class TestDiscovery {

    @Test public void openidDiscovery() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(ClientCredentials.getCixApiUrl()+"/.well-known/openid-configuration")).build();
        HttpResponse<String> response =
                  client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println(body);

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        
        Configuration config = gson.fromJson(body, Configuration.class);
        config.report();

    }

    private static class Configuration {
        private String issuer;
        private String  authorizationEndpoint;
        private Set<String> scopesSupported;

        public void report() {
            System.out.println("issuer: "+issuer);
            System.out.println("authorizationEndpoint: "+authorizationEndpoint);
            System.out.println("scopesSupported: "+scopesSupported);
        }
    }
}
