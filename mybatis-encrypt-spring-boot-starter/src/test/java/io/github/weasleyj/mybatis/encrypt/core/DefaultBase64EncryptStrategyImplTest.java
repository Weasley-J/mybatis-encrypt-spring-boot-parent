package io.github.weasleyj.mybatis.encrypt.core;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

class DefaultBase64EncryptStrategyImplTest {

    static EncryptStrategy encryptStrategy = new DefaultBase64EncryptStrategyImpl();

    String raw = "这是一个Base64的加密解密测试！Abc123,!@#$%^&*()_+,！@#￥%%……&*（）";

    @Test
    void encrypt() {
        String encrypt = encryptStrategy.encrypt(raw);
        System.out.println("encrypt = " + encrypt);
    }

    @Test
    void decrypt() {
        String encrypt = encryptStrategy.encrypt(raw);
        String decrypt = encryptStrategy.decrypt(encrypt);
        System.out.println(decrypt);
        Assert.isTrue(raw.equals(decrypt), "解密后的明文由于原始内容一致");
    }

    public String encrypt(Object plaintext) {
        Assert.notNull(plaintext, "Plaintext cannot be null");
        return Base64Utils.encodeToString(String.valueOf(plaintext).getBytes(StandardCharsets.UTF_8));
    }


    public String decrypt(Object ciphertext) {
        Assert.notNull(ciphertext, "Ciphertext cannot be null");
        return new String(Base64Utils.decodeFromString(String.valueOf(ciphertext)), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("encoded1 == encoded2")
    void func1() {
        String encoded1 = encrypt(raw);
        String encoded2 = Base64.encodeBase64String(raw.getBytes(StandardCharsets.UTF_8));
        Assert.isTrue(encoded1.equals(encoded2), "encoded1 == encoded2");
    }

    @Test
    @DisplayName("decoded1 == decoded2")
    void func2() {
        String encoded1 = encrypt(raw);
        String encoded2 = Base64.encodeBase64String(raw.getBytes(StandardCharsets.UTF_8));
        Assert.isTrue(encoded1.equals(encoded2), "encoded1 == encoded2");

        String decrypt1 = decrypt(encoded1);
        String decrypt2 = new String(Base64.decodeBase64(encoded2));
        Assert.isTrue(decrypt1.equals(decrypt2), "decrypt1 == decrypt2");
    }
}
