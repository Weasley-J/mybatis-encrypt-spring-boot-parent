package io.github.weasleyj.mybatis.encrypt.core;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
        return Base64.encodeBase64String(String.valueOf(plaintext).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(Object ciphertext) {
        Assert.notNull(ciphertext, "Ciphertext cannot be null");
        return new String(Base64.decodeBase64(String.valueOf(ciphertext)), StandardCharsets.UTF_8);
    }
}
