package uk.me.mthornton.utility;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TestStandardPaths {
    private static final ApplicationId APPLICATION_ID = new ApplicationId("uk.me.mthornton", "utility");
    
    @Test public void hasUserPaths() {
        assertNotNull(StandardPaths.getUserInstance());
    }

    @Test public void checkConfigFile() {
        Path path = StandardPaths.getUserInstance().getConfigFolder(APPLICATION_ID);
        System.out.println(path);
    }

    @Test public void checkSystemConfigFile() {
        Path path = StandardPaths.getSystemInstance().getConfigFolder(APPLICATION_ID);
        System.out.println(path);
    }
}
