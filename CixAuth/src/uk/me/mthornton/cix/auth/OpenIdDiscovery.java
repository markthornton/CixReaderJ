package uk.me.mthornton.cix.auth;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.concurrent.CompletionStage;

public class OpenIdDiscovery {
    private HttpClient client;
    private String uri;
    private Configuration configuration;
    private CompletionStage<Configuration> asyncConfiguration;
    private Gson gson;

    public OpenIdDiscovery(HttpClient client, String uri) {
        this.client = client;
        this.uri = uri;
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    public Gson getGson() {
        return gson;
    }

    public synchronized Configuration getConfiguration() {
        if (configuration == null) {
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create(uri+"/.well-known/openid-configuration")).build();
            try {
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                configuration = gson.fromJson(response.body(), Configuration.class);
            } catch (IOException | InterruptedException e) {
                throw new CommunicationException(e);
            }
        }
        return configuration;
    }

    public synchronized CompletionStage<Configuration> getConfigurationAsync() {
        if (asyncConfiguration == null) {
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create(uri+"/.well-known/openid-configuration")).build();
            asyncConfiguration = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> gson.fromJson(response.body(), Configuration.class));
        }
        return asyncConfiguration;
    }

    public static class Configuration {
        private String issuer;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private Set<String> scopesSupported;

        public String getIssuer() {
            return issuer;
        }

        public String getAuthorizationEndpoint() {
            return authorizationEndpoint;
        }

        public String getTokenEndpoint() {
            return tokenEndpoint;
        }

        public Set<String> getScopesSupported() {
            return scopesSupported;
        }

        // add extra properties as required
    }
}
