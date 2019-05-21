package uk.me.mthornton.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FormUrlEncodedContent {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    public static HttpRequest.BodyPublisher publish(Map<String, String> properties) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(body, StandardCharsets.US_ASCII);
        int n=0;
        try {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (n++ > 0) {
                    out.write('&');
                }
                out.write(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                out.write('=');
                out.write(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            out.close();
        } catch (IOException e) {
            throw new AssertionError("Unexpected exception", e);
        }
        return HttpRequest.BodyPublishers.ofByteArray(body.toByteArray());
    }
}
