package uk.me.mthornton.utility;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TestApplicationConfiguration {
    private static final ApplicationId ID = new ApplicationId("uk.me.mthornton", "testConfiguration");

    private void show(Path path) {
        System.out.println(path);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test public void strings()  throws IOException {
        ApplicationConfiguration configuration = new ApplicationConfiguration(ID);
        try {
            configuration.put("fred", "happy");
            configuration.setCompact(false);
            configuration.saveConfiguration();

            configuration = new ApplicationConfiguration(ID);
            String value = configuration.get("fred", String.class);
            assertEquals("happy", value);
            show(configuration.getFile());
        } finally {
            Files.deleteIfExists(configuration.getFile());
        }
    }

    @Test public void complex() throws IOException {
        ApplicationConfiguration configuration = new ApplicationConfiguration(ID);
        try {
            Sample object = new Sample();
            object.name = "today";
            object.price = 3.76;
            configuration.put("data", object);
            configuration.saveConfiguration();
            configuration = new ApplicationConfiguration(ID);
            Sample value = configuration.get("data", Sample.class);
            assertEquals(object.name, value.name);
            assertEquals(object.price, value.price);
            show(configuration.getFile());
        } finally {
            Files.deleteIfExists(configuration.getFile());
        }
    }

    public static class Sample {
        String name;
        double price;
    }
}
