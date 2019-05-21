package uk.me.mthornton.utility.spi;

import uk.me.mthornton.utility.ApplicationId;
import uk.me.mthornton.utility.StandardPaths;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxSystemPaths extends StandardPaths {
    private static final String CONFIG = "/etc/opt";
    private static final String CACHE = "/var/cache/opt";
    private static final String DATA = "/var/opt";

    @Override
    public Path getConfigFile(ApplicationId id, String extension) {
        return Paths.get(CONFIG, id.getProvider(), id.getName().concat(extension));
    }

    @Override
    public Path getConfigFolder(ApplicationId id) {
        return Paths.get(CONFIG, id.getProvider(), id.getName());
    }

    @Override
    public Path getCacheFolder(ApplicationId id) {
        return Paths.get(CACHE, id.getProvider(), id.getName());
    }

    @Override
    public Path getDataFolder(ApplicationId id) {
        return Paths.get(DATA, id.getProvider(), id.getName());
    }
}
