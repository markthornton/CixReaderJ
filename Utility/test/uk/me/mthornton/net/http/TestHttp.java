package uk.me.mthornton.net.http;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHttp {
    @Test
    public void basic() {
        assertEquals("Basic QWxhZGRpbjpPcGVuU2VzYW1l", Http.basicAuthorization("Aladdin", "OpenSesame"));
    }

}
