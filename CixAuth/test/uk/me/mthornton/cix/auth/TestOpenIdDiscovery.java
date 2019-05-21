package uk.me.mthornton.cix.auth;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class TestOpenIdDiscovery {
    private HttpClient client = HttpClient.newHttpClient();

    @Test
    public void discoverConfiguration() {
        OpenIdDiscovery discovery = new OpenIdDiscovery(client, ClientCredentials.getCixApiUrl());
        OpenIdDiscovery.Configuration configuration = discovery.getConfiguration();
        System.out.println("issuer: "+configuration.getIssuer());
        System.out.println("tokenEndpoint: "+configuration.getTokenEndpoint());
    }

    @Test
    public void discoverConfigurationAsync() throws ExecutionException, InterruptedException {
        OpenIdDiscovery discovery = new OpenIdDiscovery(client, ClientCredentials.getCixApiUrl());
        CompletionStage<OpenIdDiscovery.Configuration> asyncConfiguration = discovery.getConfigurationAsync();
        OpenIdDiscovery.Configuration configuration = asyncConfiguration.toCompletableFuture().get();
        System.out.println("issuer: "+configuration.getIssuer());
        System.out.println("tokenEndpoint: "+configuration.getTokenEndpoint());
    }
}
