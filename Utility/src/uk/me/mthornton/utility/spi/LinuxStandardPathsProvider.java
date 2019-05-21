package uk.me.mthornton.utility.spi;

import uk.me.mthornton.utility.StandardPaths;

public class LinuxStandardPathsProvider implements StandardPathsProvider {
    private static final boolean isUnix = "/".equals(System.getProperty("file.separator"));

    @Override
    public boolean isApplicable() {
        return isUnix;
    }

    @Override
    public int[] getVersion() {
        return new int[] {1};
    }

    @Override
    public StandardPaths getUserPaths() {
        return new LinuxUserPaths();
    }

    @Override
    public StandardPaths getSystemPaths() {
        return new LinuxSystemPaths();
    }
}
