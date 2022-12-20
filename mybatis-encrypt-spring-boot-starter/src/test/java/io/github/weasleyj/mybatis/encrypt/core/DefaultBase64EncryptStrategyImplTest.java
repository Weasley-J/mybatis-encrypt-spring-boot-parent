package io.github.weasleyj.mybatis.encrypt.core;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

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

}
