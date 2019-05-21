package uk.me.mthornton.utility;

import uk.me.mthornton.utility.spi.StandardPathsProvider;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;

/** Standard Paths in file system
 *
 */
public abstract class StandardPaths {
    private static final StandardPaths systemPaths;
    private static final StandardPaths userPaths;

    static {
        // search for most applicable path provider
        StandardPathsProvider bestProvider = null;
        ServiceLoader<StandardPathsProvider> loader = ServiceLoader.load(StandardPathsProvider.class);
        for (Iterator<StandardPathsProvider> i=loader.iterator(); i.hasNext();) {
            StandardPathsProvider provider = i.next();
            if (provider.isApplicable()) {
                if (bestProvider == null || provider.compareTo(bestProvider) > 0) {
                    bestProvider = provider;
                }
            }
        }
        if (bestProvider != null) {
            systemPaths = bestProvider.getSystemPaths();
            userPaths = bestProvider.getUserPaths();
        } else {
            systemPaths = null;
            userPaths = null;
        }
    }

    public static StandardPaths getUserInstance() {
        return userPaths;
    }

    public static StandardPaths getSystemInstance() {
        return systemPaths;
    }

    public abstract Path getConfigFile(ApplicationId id, String extension);

    public abstract Path getConfigFolder(ApplicationId id);

    public abstract Path getCacheFolder(ApplicationId id);

    public abstract Path getDataFolder(ApplicationId id);
}
