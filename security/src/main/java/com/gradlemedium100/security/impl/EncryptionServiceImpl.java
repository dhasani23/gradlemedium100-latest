package com.gradlemedium100.security.impl;

import com.gradlemedium100.security.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Implementation of EncryptionService that uses AES encryption algorithm.
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {

    private final SecretKey secretKey;
    private static final String ALGORITHM = "AES";

    /**
     * Constructor that initializes the encryption service with a secret key.
     *
     * @param secretKey Base64 encoded secret key for encryption/decryption
     */
    public EncryptionServiceImpl(@Value("${security.encryption.key:defaultEncryptionKey12345678}") String secretKey) {
        // Convert the string key to appropriate format for AES
        byte[] decodedKey = secretKey.getBytes(StandardCharsets.UTF_8);
        // Use first 16 bytes for 128-bit AES key
        byte[] key = new byte[16];
        System.arraycopy(decodedKey, 0, key, 0, Math.min(decodedKey.length, 16));
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}