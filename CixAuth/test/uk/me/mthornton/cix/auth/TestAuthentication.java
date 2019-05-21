package uk.me.mthornton.cix.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import uk.me.mthornton.net.http.FormUrlEncodedContent;
import uk.me.mthornton.net.http.Http;
import uk.me.mthornton.secrets.Secrets;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAuthentication {
    private static final String USER = "mthorn";
    private HttpClient client;
    private OpenIdDiscovery discovery;
    private Secrets secrets;

    @BeforeAll public void setup() {
        client = HttpClient.newHttpClient();
        discovery = new OpenIdDiscovery(client, ClientCredentials.getCixApiUrl());
        secrets = Secrets.getSecrets("CIX");
    }

    @Test
    public void getToken() throws IOException, InterruptedException {

        Map<String,String> fields = new HashMap<>();
        fields.put("grant_type", "password");
        fields.put("scope", "cixApi3");
        fields.put("username", USER);
        fields.put("password", secrets.getSecretString(USER));
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(discovery.getConfiguration().getTokenEndpoint()));
        builder.header(Http.CONTENT_TYPE, FormUrlEncodedContent.CONTENT_TYPE);
        builder.header(Http.AUTHORIZATION, Http.basicAuthorization(ClientCredentials.getClientId(), ClientCredentials.getClientSecret()));
        builder.POST(FormUrlEncodedContent.publish(fields));
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("Response: "+response.statusCode());
        System.out.println("body: "+response.body());
        for (Map.Entry<String, List<String>> header: response.headers().map().entrySet()) {
            for (String value: header.getValue()) {
                System.out.println(header.getKey()+": "+value);
            }
        }
    }

}
