package uk.me.mthornton.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

/** Store application configuration data.
 * The data is stored in JSON format. Parsing is delayed until you first request an entry - you must specify the
 * type of the value expected. Values are serialized using the <a href="https://github.com/google/gson">GSON</a> package.
 */
public class ApplicationConfiguration {
    private static final String COMPACT = "json.compact";

    private final ApplicationId applicationId;
    private final Path file;
    private final Map<String, Object> entries = new HashMap<>();
    private final Map<String, JsonElement> jsonEntries = new HashMap<>();
    private final Map<String, Type> entryTypes = new HashMap<>();
    private Gson gson;
    private boolean modified;
    private boolean compact = true;

    public ApplicationConfiguration(ApplicationId applicationId) {
        this.applicationId = applicationId;
        file = StandardPaths.getUserInstance().getConfigFile(applicationId, ".json");
        gson = new GsonBuilder().create();
        loadConfiguration();
    }

    private void loadConfiguration() {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonParser parser = new JsonParser();
            JsonReader json = gson.newJsonReader(reader);
            json.beginObject();
            while (json.hasNext()) {
                String name = json.nextName();
                if (name.equals(COMPACT)) {
                    compact = json.nextBoolean();
                } else {
                    jsonEntries.put(name, parser.parse(json));
                }
            }
            json.endObject();
        } catch (NoSuchFileException e) {
            // ignore
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ApplicationId getApplicationId() {
        return applicationId;
    }

    public Path getFile() {
        return file;
    }

    /** is compact form used for JSON serialization.
     *  Default is true. This property is also persisted.
     * @return
     */
    public synchronized boolean isCompact() {
        return compact;
    }

    public synchronized void setCompact(boolean compact) {
        if (this.compact != compact) {
            this.compact = compact;
            modified = true;
        }
    }

    public synchronized void saveConfiguration() {
        if (!modified) {
            return;
        }
        modified = false;
        // Generate json for new or modified entries
        for (Map.Entry<String, Object> entry: entries.entrySet()) {
            if (!jsonEntries.containsKey(entry.getKey())) {
                Type type = entryTypes.get(entry.getKey());
                JsonElement element = type == null ? gson.toJsonTree(entry.getValue()) : gson.toJsonTree(entry.getValue(), type);
                jsonEntries.put(entry.getKey(), element);
            }
        }
        // use a TreeSet to maintain a stable ordering of entries
        Set<String> keys = new TreeSet<>(jsonEntries.keySet());
        try (Writer out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JsonWriter json = gson.newJsonWriter(out);
            if (!compact) {
                json.setIndent("  ");   // make result (sort of) readable
            }
            json.beginObject();
            if (!compact) {
                json.name(COMPACT);
                json.value(false);
            }
            for (String key: keys) {
                json.name(key);
                gson.toJson(jsonEntries.get(key), json);
            }
            json.endObject();
            json.close();
        } catch (IOException e) {

        }
    }

    /** get a configuration property.
     * Note that if the type is mutable, then you must call the put method to notify this class that the value has changed.
     * This method requires a non parameterised type.
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    public <T> T get(String name, Class<T> type) {
        return get(name, type, null);
    }

    public synchronized <T> T get(String name, Class<T> type, T defaultValue) {
        T value = type.cast(entries.get(name));
        if (value == null ) {
            JsonElement element = jsonEntries.get(name);
            if (element == null) {
                return defaultValue;
            }
            value = gson.fromJson(element, type);
            entries.put(name, value);
        }
        return value;
    }

    /** get a generic configuration property.
     * @see <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types">Serializing and Deserializing Generic Types</a>
     * @param name property name
     * @param type type token for the property
     * @param <T>
     * @return
     */
    public <T> T get(String name, Type type) {
        return get(name, type, null);
    }

    public synchronized <T> T get(String name, Type type, T defaultValue) {
        T value = (T)entries.get(name);
        if (value == null ) {
            JsonElement element = jsonEntries.get(name);
            if (element == null) {
                return defaultValue;
            }
            value = gson.fromJson(element, type);
            entries.put(name, value);
            entryTypes.put(name, type);
        }
        return value;
    }

    public synchronized void put(String name, Object value) {
        jsonEntries.remove(name);
        entries.put(name, value);
        entryTypes.remove(name);
        modified = true;
    }

    /** put a value of a generic type.
     *
     * @param name
     * @param value
     * @param type
     */
    public synchronized void put(String name, Object value, Type type) {
        jsonEntries.remove(name);
        entries.put(name, value);
        entryTypes.put(name, type);
        modified = true;
    }

    /** Remove a property */
    public synchronized boolean remove(String name) {
        boolean result = entries.remove(name) != null | jsonEntries.remove(name) != null;
        if (result) {
            entryTypes.remove(name);
            modified = true;
        }
        return result;
    }
}
