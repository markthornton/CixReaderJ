package uk.me.mthornton.cix.auth;

import uk.me.mthornton.utility.ApplicationId;

public class ClientCredentials {
    private static final ApplicationId APPLICATION_ID = new ApplicationId("uk.me.mthornton", "CixReaderJ");
    public static String getCixApiUrl() {
        return "https://api.cix.uk";
    }

    public static String getSecretContext() {
        return "CIX";
    }

    public static String getClientId() {
        return "CixReaderJ";
    }

    public static String getClientSecret() {
        return "I29yBa96TWpc";
    }

    public static ApplicationId getApplicationId() {
        return APPLICATION_ID;
    }

    public static String getUserProperty() {
        return "cix.user";
    }
}
