package io.github.weasleyj.mybatis.encrypt.core;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

/**
 * The default base64 encrypt strategy implementation
 *
 * @author weasley
 * @version 1.0.0
 */
@Component
@ConditionalOnBean(annotation = {EnableMybatisEncryption.class})
public class DefaultBase64EncryptStrategyImpl implements EncryptStrategy {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String encrypt(Object plaintext) {
        Assert.notNull(plaintext, "Plaintext cannot be null");
        return Base64Utils.encodeToString(String.valueOf(plaintext).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(Object ciphertext) {
        Assert.notNull(ciphertext, "Ciphertext cannot be null");
        return new String(Base64Utils.decodeFromString(String.valueOf(ciphertext)), StandardCharsets.UTF_8);
    }
}
