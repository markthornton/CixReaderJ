package uk.me.mthornton.cix.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.me.mthornton.net.http.FormUrlEncodedContent;
import uk.me.mthornton.net.http.Http;
import uk.me.mthornton.secrets.Secrets;
import uk.me.mthornton.utility.CommonScheduler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class CixAuthentication {
    private final HttpClient client;
    private final OpenIdDiscovery discovery;
    private final Secrets secrets = Secrets.getSecrets("CIX");
    private final Map<String, CompletionStage<String>> tokenRequests = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(CixAuthentication.class);

    public CixAuthentication(HttpClient client) {
        this.client = client;
        discovery = new OpenIdDiscovery(client, ClientCredentials.getCixApiUrl());
    }

    public HttpClient getClient() {
        return client;
    }

    public OpenIdDiscovery getDiscovery() {
        return discovery;
    }

    public Secrets getSecrets() {
        return secrets;
    }

    public synchronized CompletionStage<String> getAuthenticationToken(String user) {
        CompletionStage<String> request = tokenRequests.get(user);
        if (request == null) {
            request = sendRequest(user);
        }
        return request;
    }

    private CompletionStage<String> sendRequest(String user) {
        // obtain the password early so that any exception is immediate
        String password = secrets.getSecretString(user);
        CompletionStage<HttpResponse<String>> responseAsync = discovery.getConfigurationAsync().thenCompose(configuration -> {
            Map<String,String> fields = new HashMap<>();
            fields.put("grant_type", "password");
            fields.put("scope", "cixApi3");
            fields.put("username", user);
            fields.put("password", password);
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(configuration.getTokenEndpoint()));
            builder.header(Http.CONTENT_TYPE, FormUrlEncodedContent.CONTENT_TYPE);
            builder.header(Http.AUTHORIZATION, Http.basicAuthorization(ClientCredentials.getClientId(), ClientCredentials.getClientSecret()));
            builder.POST(FormUrlEncodedContent.publish(fields));
            logger.info("Sending token request");
            return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString());
        });
        final RemoveAction action = new RemoveAction(user);
        final CompletionStage<String> result = responseAsync.thenApply(response -> {
            logger.info("Received token response {}", response.statusCode());
            TokenResult token = discovery.getGson().fromJson(response.body(), TokenResult.class);
            // schedule removal of this result before it expires
            CommonScheduler.scheduledExecutor().schedule(action, token.getExpiresIn()-token.getExpiresIn()/20, TimeUnit.SECONDS);
            return token.getAccessToken();
        });
        tokenRequests.put(user, result);
        action.setResultAsync(result);
        return result;
    }

    public synchronized boolean removeToken(String user, CompletionStage<String> resultAsync) {
        return tokenRequests.remove(user, resultAsync);
    }

    private class RemoveAction implements Runnable {
        private final String user;
        private CompletionStage<String> resultAsync;

        public RemoveAction(String user) {
            this.user = user;
        }

        public void setResultAsync(CompletionStage<String> resultAsync) {
            this.resultAsync = resultAsync;
        }

        @Override
        public void run() {
            removeToken(user, resultAsync);
        }
    }

    private static class TokenResult {
        private String accessToken;
        private long expiresIn;
        private String tokenType;

        public String getAccessToken() {
            return accessToken;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }
    }
}
