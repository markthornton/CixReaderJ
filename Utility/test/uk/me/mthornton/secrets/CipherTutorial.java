package uk.me.mthornton.secrets;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CipherTutorial {
    private static final String KEY_ALGORITHM ="AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private SecretKey key;
    private Base64.Encoder encoder = Base64.getEncoder();
    private Base64.Decoder decoder = Base64.getDecoder();
    private Cipher cipher;
    private SecureRandom random;

    public static void main(String[] args) {
        try {
            new CipherTutorial().run();
            System.out.println("Completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        random = SecureRandom.getInstanceStrong();
        System.out.println("SecureRandom: "+random.getAlgorithm());
        generateKey();
        persistKey();
        testEncryption("password");
        // repeat for a variety of string lengths
        testEncryption("The lazy brown fox jumped over the moon");
    }

    private void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);
        key = generator.generateKey();
        System.out.println("Created key, "+key.getAlgorithm()+", format="+key.getFormat()+": "+encoder.encodeToString(key.getEncoded()));
    }

    private void persistKey() {
        byte[] encoded = key.getEncoded();
        SecretKeySpec spec = new SecretKeySpec(encoded, KEY_ALGORITHM);
        System.out.println("Persisted: "+spec.equals(key));
    }

    private void testEncryption(String text) throws Exception {
        EncodedData data = encryptText(text);
        String result = decryptText(data);
        if (!text.equals(result)) {
            System.out.println("Encryption failed: \""+text+"\" != \""+result+"\"");
        }
    }

    private EncodedData encryptText(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (cipher == null) {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        }
        byte[] iv = new byte[cipher.getBlockSize()];
        random.nextBytes(iv);
        IvParameterSpec ivParameter = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameter);
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] encoded = cipher.doFinal(textBytes);
        System.out.println("IV="+encoder.encodeToString(iv)+" encoded="+encoder.encodeToString(encoded));
        return new EncodedData(iv, encoded);
    }

    private String decryptText(EncodedData data) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(data.getIv()));
        byte[] textBytes = cipher.doFinal(data.getBytes());
        String result =  new String(textBytes, StandardCharsets.UTF_8);
        System.out.println("Decoded as: \""+result+"\"");
        return result;
    }

    private static class EncodedData {
        private byte[] iv;
        private byte[] bytes;

        public EncodedData(byte[] iv, byte[] bytes) {
            this.iv = iv;
            this.bytes = bytes;
        }

        public byte[] getIv() {
            return iv;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }
}
