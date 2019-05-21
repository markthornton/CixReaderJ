package uk.me.mthornton.secrets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.me.mthornton.utility.ApplicationId;
import uk.me.mthornton.utility.StandardPaths;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.*;

/** stuff that we don't want to store in plain text.
 * On a linux system should set access to remove group and other access from all files.
 */
public class Secrets {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String KEY_ALGORITHM ="AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final ApplicationId APPLICATION_ID = new ApplicationId("uk.me.mthornton", "vault");
    private static final Map<String, Secrets> secretMap = new HashMap<>();
    private static final Path secretsFolder = StandardPaths.getUserInstance().getDataFolder(APPLICATION_ID);
    private static final Logger logger = LogManager.getLogger(Secrets.class);

    public static synchronized Secrets getSecrets(String context) {
        Secrets secrets = secretMap.get(context);
        if (secrets == null) {
            secrets = new Secrets(context);
            secretMap.put(context, secrets);
        }
        return secrets;
    }

    private final Path secretsFile;
    private Properties secrets;
    private Cipher cipher;
    private Base64.Decoder decoder;
    private CharsetDecoder charDecoder;
    private CharsetEncoder charEncoder;
    private Base64.Encoder encoder;
    private SecureRandom random;

    public static void erase(char[] value) {
        Arrays.fill(value, '\0');
    }

    private Secrets(String context) {
        secretsFile = secretsFolder.resolve(context.concat(".properties"));
        secrets = new Properties();
        // read the file if it exists
        try {
            InputStream in = Files.newInputStream(secretsFile);
            secrets.load(new BufferedInputStream(in));
            in.close();
        } catch (NoSuchFileException e) {
            // normal
        } catch (IOException e) {
            logger.warn("Failed to read secrets file {}: {}", secretsFile, e);
        }
    }

    private synchronized CharBuffer getSecretCharBuffer(String key) {
        String value = secrets.getProperty(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        int x = value.indexOf(',');
        if (x <= 0) {
            throw new NoSuchElementException();
        }
        if (decoder == null) {
            decoder = Base64.getDecoder();
        }
        byte[] iv = decoder.decode(value.substring(0, x));
        byte[] cipherText = decoder.decode(value.substring(x+1));
        try {
            if (cipher == null) {
                cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            }
            cipher.init(Cipher.DECRYPT_MODE, Master.getKey(), new IvParameterSpec(iv));
            byte[] plain = cipher.doFinal(cipherText);
            if (charDecoder == null) {
                charDecoder = CHARSET.newDecoder();
            }
            return charDecoder.decode(ByteBuffer.wrap(plain));
        } catch (Exception e) {
            throw new SecretVaultException(e);
        }
    }

    public char[] getSecret(String key) {
        CharBuffer buffer = getSecretCharBuffer(key);
        char[] result = new char[buffer.length()];
        buffer.get(result);
        // todo: erase the buffer
        return result;
    }

    public String getSecretString(String key) {
        return getSecretCharBuffer(key).toString();
    }

    private synchronized void setSecret(String key, CharBuffer value) {
        if (charEncoder == null) {
            charEncoder = CHARSET.newEncoder();
        }
        try {
            ByteBuffer plain = charEncoder.encode(value);
            if (cipher == null) {
                cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            }
            if (random == null) {
                random = SecureRandom.getInstanceStrong();
            }
            byte[] iv = new byte[cipher.getBlockSize()];
            random.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, Master.getKey(), new IvParameterSpec(iv));
            int n = plain.remaining() - plain.remaining() % cipher.getBlockSize() + 2 * cipher.getBlockSize();
            ByteBuffer cipherText = ByteBuffer.allocate(n);
            n = cipher.doFinal(plain, cipherText);
            cipherText.flip();
            byte[] bytes = new byte[n];
            cipherText.get(bytes);
            if (encoder == null) {
                encoder = Base64.getEncoder();
            }
            secrets.setProperty(key, encoder.encodeToString(iv) + "," + encoder.encodeToString(bytes));
        } catch (Exception e) {
            throw new SecretVaultException(e);
        }
        updateSecretsFile();
    }

    private void updateSecretsFile() {
        try {
            logger.info("Updating secrets file {}", secretsFile);
            OutputStream out = new BufferedOutputStream(Files.newOutputStream(secretsFile));
            secrets.store(out, null);
            out.close();
        } catch (IOException e) {
            throw new SecretVaultException(e);
        }
    }

    public void setSecret(String key, char[] value) {
        setSecret(key, CharBuffer.wrap(value));
    }

    public void setSecret(String key, String value) {
        setSecret(key, CharBuffer.wrap(value));
    }

    public void removeSecret(String key) {
        if (secrets.remove(key) != null) {
            updateSecretsFile();
        }
    }

    private static class Master {
        private static final Master instance = new Master();

        private SecretKey key;
        private Exception cause;

        static SecretKey getKey() {
            if (instance.key != null) {
                return instance.key;
            }
            throw new RuntimeException(instance.cause);
        }

        private Master() {
            Path masterFile = secretsFolder.resolve("master.x");
            logger.info("Loading master key {}", masterFile);
            try {
                try {
                    byte[] keyBytes = Files.readAllBytes(masterFile);
                    // TODO: if too short, retry read
                    key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
                } catch (NoSuchFileException e) {
                    logger.info("Creating new master key {}", masterFile);
                    KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);
                    key = generator.generateKey();
                    byte[] keyBytes = key.getEncoded();
                    Files.createDirectories(secretsFolder);
                    Files.write(masterFile, keyBytes, StandardOpenOption.CREATE_NEW);
                    // todo: if failed due to existing file, then try to read
                }
            } catch (Exception e) {
                logger.warn("Failed to create master key {}: {}", masterFile, e);
                cause = e;
            }
        }
    }
}
