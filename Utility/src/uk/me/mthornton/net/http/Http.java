package uk.me.mthornton.net.http;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Http {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";

    public static String basicAuthorization(String user, String password, Charset charset) {
        CharsetEncoder encoder = charset.newEncoder();
        CharBuffer chars = CharBuffer.allocate(user.length()+password.length()+1);
        chars.put(user);
        chars.put(':');
        chars.put(password);
        chars.flip();
        try {
            ByteBuffer byteBuffer = encoder.encode(chars);
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            return "Basic " + Base64.getEncoder().encodeToString(bytes);
        } catch (CharacterCodingException e) {
            throw new EncodingException(e);
        }
    }

    public static String basicAuthorization(String user, String password) {
        return basicAuthorization(user, password, StandardCharsets.UTF_8);
    }
    
    public static String bearerAuthorization(String token) {
        return "Bearer ".concat(token);
    }
}
