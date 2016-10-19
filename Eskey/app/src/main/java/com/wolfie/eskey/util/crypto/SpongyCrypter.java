package com.wolfie.eskey.util.crypto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

public class SpongyCrypter implements Crypter {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static final String SALT_GENERATION_ALGORITHM = "SHA1PRNG";
    public static final String KEY_GENERATION_ALGORITHM = "AES";
    public static final String SECRET_KEY_FACTORY_PROVIDER = "SC";
    public static final String CIPHER_PROVIDER = "SC";

    public static final String STRONG_SECRET_KEY_FACTORY_ALGORITHM = "PBEWithSHA1And256BitAES-CBC-BC";
    public static final String MEDIUM_SECRET_KEY_FACTORY_ALGORITHM = "PBEWithMD5And128BitAES-CBC-OpenSSL";
//    private static String algorithm = "PBEWithSHA1And128BitAES-CBC-BC-CBC-BC";     // Slower

    public static final int ITERATION_COUNT = 20;
    public static final int SALT_SIZE_IN_BYTES = 8;

    private String mSecretKeyFactoryAlgorithm;
    private SecretKeyFactory mSecretKeyFactory;
    private Cipher mCipher;            // was called pbeCipher
    private SecretKey mSecretKey;      // was called pbeKey
    private PBEParameterSpec mPbeParameterSpec;

    public static SpongyCrypter makeStrong(String salt, String password) {
        return make(salt, password, STRONG_SECRET_KEY_FACTORY_ALGORITHM);
    }

    public static SpongyCrypter makeMedium(String salt, String password) {
        return make(salt, password, MEDIUM_SECRET_KEY_FACTORY_ALGORITHM);
    }

    private static SpongyCrypter make(String salt, String password, String secretKeyFactoryAlgorithm) {
        SpongyCrypter crypter = new SpongyCrypter(salt, secretKeyFactoryAlgorithm);
        crypter.setPassword(password);
        return crypter;
    }

    private SpongyCrypter(String saltString, String secretKeyFactoryAlgorithm)  {
        byte[] salt = fromHexString(saltString);
        if (salt.length != 8) {
            throw new RuntimeException("bad salt has wrong length: " + salt.length);
        }

        mSecretKeyFactoryAlgorithm = secretKeyFactoryAlgorithm;
        mPbeParameterSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
        try {
            mSecretKeyFactory = SecretKeyFactory.getInstance(mSecretKeyFactoryAlgorithm, SECRET_KEY_FACTORY_PROVIDER);
        } catch (NoSuchAlgorithmException | NoSuchProviderException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Set the password (which could be user generated or generateMasterKey generated) to
     * be used as an encryption key.  This method must be called before encrypt or decrypt.
     */
    private void setPassword(String password) {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        try {
            mSecretKey = mSecretKeyFactory.generateSecret(pbeKeySpec);
            mCipher = Cipher.getInstance(mSecretKeyFactoryAlgorithm, CIPHER_PROVIDER);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException
                | NoSuchPaddingException | NoSuchProviderException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Generate a new salt (returned as a hex string)
     */
    @NonNull
    public static String generateSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance(SALT_GENERATION_ALGORITHM);
            byte[] salt = new byte[SALT_SIZE_IN_BYTES];
            sr.nextBytes(salt);
            return toHexString(salt);
        } catch (NoSuchAlgorithmException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Generate a new master key (returned as a hex string)
     */
    @NonNull
    public static String generateMasterKey() {
        try {
            KeyGenerator keygen;
            keygen = KeyGenerator.getInstance(KEY_GENERATION_ALGORITHM);
            keygen.init(256);
            SecretKey genDesKey = keygen.generateKey();
            return toHexString(genDesKey.getEncoded());
        } catch (NoSuchAlgorithmException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * The number of chars in the returned string will be
     * twice the number of bytes in the input parameter
     */
    @NonNull
    public static String toHexString(byte bytes[]) {

        StringBuffer retString = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            String s = Integer.toHexString(0x0100 + (bytes[i] & 0x00FF));
            retString.append(s.substring(1));
        }
        return retString.toString();
    }

    /**
     * "0aFF00" ==> byte[] {10, 0xff, 0}
     */
    public static byte[] fromHexString(String hexString)
            throws StringIndexOutOfBoundsException, NumberFormatException {

        byte[] bytes = new byte[hexString.length() / 2];
        for (int j = 0, i = 0; i < hexString.length(); i += 2) {
            String hexByte = hexString.substring(i, i + 2);
            bytes[j++] = Integer.decode("0x" + hexByte).byteValue();
        }
        return bytes;
    }

    /**
     * Encrypt a plaintext, using the previously specified salt, algorithm and password.
     * A null or empty plaintext will return the empty string.
     * @throws RuntimeException if an internal error occurs.
     */
    @NonNull
    public String encrypt(@Nullable String plainText) {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }
        try {
            mCipher.init(Cipher.ENCRYPT_MODE, mSecretKey, mPbeParameterSpec);
            byte[] cipherText = mCipher.doFinal(plainText.getBytes());
            return toHexString(cipherText);
        } catch (IllegalBlockSizeException | BadPaddingException
                | InvalidAlgorithmParameterException | InvalidKeyException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Decrypt a cipherText, using the previously specified salt, algorithm and password.
     * A null cipherText will return the empty string.
     * If the cipherText fails to decrypt or an internal error occurs, then returns null.
     */
    public String decrypt(@Nullable String cipherText) {
        if (cipherText == null || cipherText.length() == 0) {
            return "";
        }
        try {
            byte[] input = fromHexString(cipherText);
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKey, mPbeParameterSpec);
            byte[] plaintext = mCipher.doFinal(input);
            return new String(plaintext);
        } catch (IllegalBlockSizeException | BadPaddingException
                | InvalidAlgorithmParameterException | InvalidKeyException exc) {
            return null;
        }

    }

}
