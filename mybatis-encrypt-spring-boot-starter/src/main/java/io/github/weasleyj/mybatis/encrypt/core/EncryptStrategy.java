package io.github.weasleyj.mybatis.encrypt.core;

import io.github.weasleyj.mybatis.encrypt.annotation.Encryption;
import io.github.weasleyj.mybatis.encrypt.constant.EncryptType;
import io.github.weasleyj.mybatis.encrypt.exception.MybatisEncryptException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

import static io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptConfigurer.STRATEGY_CLIENTS;

/**
 * The interface for encrypt strategy
 *
 * @author weasley
 * @version 1.0.0
 */
public interface EncryptStrategy {
    /**
     * When using encrypted fields as query fields, you may need to encrypt plaintext fields before they can be recognized by the database as query parameters
     *
     * @param plainBean The bean to encrypt its fields to ciphertext which annotated by {@link Encryption}
     * @param <E>       The type of raw bean
     * @return The bean with fields encrypt to ciphertext
     */
    static <E> E covert(E plainBean, EncryptType encryptType) {
        Assert.notNull(plainBean, "Plain bean must be not null");
        Assert.notNull(encryptType, "Encrypt type must be not null");
        if (plainBean.getClass() == Object.class) return plainBean;
        List<Field> encryptionFields = FieldUtils.getFieldsListWithAnnotation(plainBean.getClass(), Encryption.class);
        if (CollectionUtils.isEmpty(encryptionFields)) return plainBean;
        EncryptStrategy encryptStrategy = STRATEGY_CLIENTS.get(encryptType);
        if (null == encryptStrategy) return plainBean;
        for (Field field : encryptionFields) {
            try {
                Object plaintextFieldValue = FieldUtils.readField(field, plainBean, true);
                if (null == plaintextFieldValue) continue;
                String encrypt = encryptStrategy.encrypt(plaintextFieldValue);
                FieldUtils.writeField(field, plainBean, encrypt, true);
            } catch (IllegalAccessException e) {
                throw new MybatisEncryptException("Covert plain bean to cipher bean error:", e);
            }
        }
        return plainBean;
    }

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
     * To decrypt an encrypted text that human-unreadable
     *
     * @param ciphertext The encrypted text to decrypt
     * @return The decrypted text that human-readable
     */
    default String decrypt(Object ciphertext) {
        return null;
    }
}
