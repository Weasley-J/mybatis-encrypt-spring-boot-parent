package io.github.weasleyj.mybatis.encrypt.core;

/**
 * The interface for encrypt strategy
 *
 * @author weasley
 * @version 1.0.0
 */
public interface EncryptStrategy {
    /**
     * To encrypt a raw text that human-readable
     *
     * @param plaintext The raw text
     * @return The encrypted text
     */
    default String encrypt(Object plaintext) {
        return null;
    }

    /**
     * To decrypt an encrypted test that human-unreadable
     *
     * @param ciphertext The encrypted text to decrypt
     * @return The decrypted text that human-readable
     */
    default String decrypt(Object ciphertext) {
        return null;
    }

}
