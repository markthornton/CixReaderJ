package uk.me.mthornton.utility.spi;

import uk.me.mthornton.utility.StandardPaths;

public interface StandardPathsProvider extends Comparable<StandardPathsProvider> {
    boolean isApplicable();
    int[] getVersion();
    StandardPaths getUserPaths();
    StandardPaths getSystemPaths();

    @Override
    default int compareTo(StandardPathsProvider other) {
        int[] thisVersion = getVersion();
        int[] otherVersion = other.getVersion();

        int m = Math.min(thisVersion.length, otherVersion.length);
        for (int i=0; i<m; i++) {
            int z = Integer.compare(thisVersion[i], otherVersion[i]);
            if (z != 0)
                return z;
        }
        return Integer.compare(thisVersion.length, otherVersion.length);
    }
}
