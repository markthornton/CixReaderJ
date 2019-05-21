package uk.me.mthornton.cix.model;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.me.mthornton.cix.auth.CixAuthentication;
import uk.me.mthornton.cix.auth.ClientCredentials;
import uk.me.mthornton.net.http.Http;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestApi {
    private static final String USER = "mthorn";
    private HttpClient client;
    private CixAuthentication authentication;
    private Gson gson;

    @BeforeAll public void setup() {
        client = HttpClient.newHttpClient();
        authentication = new CixAuthentication(client);
        gson = Converters.registerAll(new GsonBuilder()).create();
    }

    @Test public void getSubscribedForums() throws ExecutionException, InterruptedException {
        CompletionStage<HttpResponse<String>> requestAsync = authentication.getAuthenticationToken(USER).thenCompose(token -> {
            // BASIC does not return topics, moderators or participants
            URI uri = URI.create(ClientCredentials.getCixApiUrl().concat("/v3.0/User/subscriptions/BASIC"));
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
            builder.header(Http.AUTHORIZATION, Http.bearerAuthorization(token));
            return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString());
        });
        CompletionStage<List<Forum>> forumsAsync = requestAsync.thenApply(response ->
            gson.fromJson(response.body(), new TypeToken<List<Forum>>() {}.getType())
        );
        List<Forum> forums = forumsAsync.toCompletableFuture().get();
        System.out.println(forums.size()+ " subscribed forums");
    }

    @Test public void streamSubscribedForums()  throws ExecutionException, InterruptedException {
        CompletionStage<HttpResponse<InputStream>> requestAsync = authentication.getAuthenticationToken(USER).thenCompose(token -> {
            // BASIC does not return topics, moderators or participants
            URI uri = URI.create(ClientCredentials.getCixApiUrl().concat("/v3.0/User/subscriptions/BASIC"));
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
            builder.header(Http.AUTHORIZATION, Http.bearerAuthorization(token));
            return client.sendAsync(builder.build(), responseInfo -> HttpResponse.BodySubscribers.ofInputStream());
        });
        CompletionStage<List<Forum>> forumsAsync = requestAsync.thenApply(response ->
            gson.fromJson(new InputStreamReader(response.body(), StandardCharsets.UTF_8), new TypeToken<List<Forum>>() {}.getType())
        );
        List<Forum> forums = forumsAsync.toCompletableFuture().get();
        System.out.println(forums.size()+ " subscribed forums");
        for (Forum f: forums) {
            System.out.println(f.getName());
        }
    }
}
