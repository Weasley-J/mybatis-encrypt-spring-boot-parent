package io.github.weasleyj.mybatis.encrypt.core;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import io.github.weasleyj.mybatis.encrypt.exception.MybatisEncryptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties.AesProperties;

/**
 * The default AES encrypt strategy implementation
 *
 * @author weasley
 * @version 1.0.0
 */
@Component
@ConditionalOnBean(annotation = {EnableMybatisEncryption.class})
public class DefaultAesEncryptStrategyImpl implements EncryptStrategy {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * The configuration properties of AES
     */
    private final AesProperties aesProperties;

    public DefaultAesEncryptStrategyImpl(AesProperties aesProperties) {
        this.aesProperties = aesProperties;
    }

    @Override
    public String encrypt(Object plaintext) {
        Assert.notNull(plaintext, "Plaintext cannot be null");
        try {
            SecretKey secretKey = new SecretKeySpec(aesProperties.getSecretKey().getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            @SuppressWarnings({"all"}) IvParameterSpec ivSpec = new IvParameterSpec(aesProperties.getKeyIv().getBytes(StandardCharsets.UTF_8));
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = String.valueOf(plaintext).getBytes(StandardCharsets.UTF_8);
            int dataByteLength = dataBytes.length;
            if (dataByteLength % blockSize != 0) {
                dataByteLength = dataByteLength + (blockSize - (dataByteLength % blockSize));
            }
            byte[] newPlaintext = new byte[dataByteLength];
            System.arraycopy(dataBytes, 0, newPlaintext, 0, dataBytes.length);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(newPlaintext);
            String encoded = Base64.getEncoder().encodeToString(encrypted);
            if (logger.isDebugEnabled()) {
                logger.debug("plaintext: {}, ciphertext: {}", plaintext, encoded);
            }
            return encoded;
        } catch (Exception e) {
            throw new MybatisEncryptException("AEC encrypt exception:", e);
        }
    }

    @Override
    public String decrypt(Object ciphertext) {
        Assert.notNull(ciphertext, "Ciphertext cannot be null");
        try {
            byte[] decodeFromBase64 = Base64.getDecoder().decode(String.valueOf(ciphertext));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(aesProperties.getSecretKey().getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            @SuppressWarnings({"all"}) IvParameterSpec ivSpec = new IvParameterSpec(aesProperties.getKeyIv().getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] originalBytes = cipher.doFinal(decodeFromBase64);
            String decrypted = new String(originalBytes, StandardCharsets.UTF_8).trim();
            if (logger.isDebugEnabled()) {
                logger.debug("ciphertext: {}, plaintext: {}", ciphertext, decrypted);
            }
            return decrypted;
        } catch (Exception e) {
            throw new MybatisEncryptException("AEC decrypt exception:", e);
        }
    }
}
