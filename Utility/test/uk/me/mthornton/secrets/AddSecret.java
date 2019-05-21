package uk.me.mthornton.secrets;

public class AddSecret {
    public static void main(String[] args) {
        String context = args[0];
        String key = args[1];
        String value = args[2];
        Secrets secrets = Secrets.getSecrets(context);
        secrets.setSecret(key, value);
        System.out.println("Set secret; context="+context+", key="+key);
    }
}
