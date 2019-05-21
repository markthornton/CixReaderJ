package uk.me.mthornton.utility;

import java.util.Objects;

public class ApplicationId {
    private final String provider;
    private final String name;

    public ApplicationId(String provider, String name) {
        this.provider = provider;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationId that = (ApplicationId) o;
        return provider.equals(that.provider) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, name);
    }

    public String getProvider() {
        return provider;
    }

    public String getName() {
        return name;
    }
}
