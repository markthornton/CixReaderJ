package uk.me.mthornton.cix.auth;

import uk.me.mthornton.secrets.Secrets;
import uk.me.mthornton.utility.ApplicationConfiguration;

/** Set CIX credentials.
 * Update the application configuration file and secrets store with the given information.
 */
public class SetCixCredentials {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("SetCixCredentials <user> <password>");
        } else {
            ApplicationConfiguration configuration = new ApplicationConfiguration(ClientCredentials.getApplicationId());
            configuration.put(ClientCredentials.getUserProperty(), args[0]);
            configuration.saveConfiguration();
            Secrets secrets = Secrets.getSecrets(ClientCredentials.getSecretContext());
            secrets.setSecret(args[0], args[1]);
        }
    }
}
