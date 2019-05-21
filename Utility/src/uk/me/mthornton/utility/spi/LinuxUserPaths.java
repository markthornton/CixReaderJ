package uk.me.mthornton.utility.spi;

import uk.me.mthornton.utility.ApplicationId;
import uk.me.mthornton.utility.StandardPaths;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxUserPaths extends StandardPaths {
    private static final String CONFIG = ".config";
    private static final String CACHE = ".cache";

    private final Path homePath;
    private final String home;

    LinuxUserPaths() {
        String path = System.getProperty("user.home");
        homePath = Paths.get(path);
        home = path;
    }

    @Override
    public Path getConfigFile(ApplicationId id, String extension) {
        return Paths.get(home, CONFIG, id.getProvider(), id.getName().concat(extension));
    }

    @Override
    public Path getConfigFolder(ApplicationId id) {
        return Paths.get(home, CONFIG, id.getProvider(), id.getName());
    }

    @Override
    public Path getCacheFolder(ApplicationId id) {
        return Paths.get(home, CACHE, id.getProvider(), id.getName());
    }

    @Override
    public Path getDataFolder(ApplicationId id) {
        return getConfigFolder(id);
    }
}
