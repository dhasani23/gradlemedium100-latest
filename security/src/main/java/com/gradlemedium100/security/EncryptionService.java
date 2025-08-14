package com.gradlemedium100.security;

/**
 * Service interface for encryption and decryption operations.
 * This interface defines methods for securely encrypting and decrypting sensitive data.
 */
public interface EncryptionService {

    /**
     * Encrypts the provided plain text.
     *
     * @param plainText The text to encrypt
     * @return The encrypted text
     * @throws Exception If encryption fails
     */
    String encrypt(String plainText) throws Exception;
    
    /**
     * Decrypts the provided encrypted text.
     *
     * @param encryptedText The text to decrypt
     * @return The decrypted text
     * @throws Exception If decryption fails
     */
    String decrypt(String encryptedText) throws Exception;
}