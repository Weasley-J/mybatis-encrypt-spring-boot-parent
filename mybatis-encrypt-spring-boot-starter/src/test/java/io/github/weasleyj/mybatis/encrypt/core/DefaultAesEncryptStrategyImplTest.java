package io.github.weasleyj.mybatis.encrypt.core;

import io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties;
import org.junit.jupiter.api.Test;

class DefaultAesEncryptStrategyImplTest {

    static EncryptStrategy encryptStrategy = new DefaultAesEncryptStrategyImpl(
            new MybatisEncryptProperties.AesProperties()
                    .setSecretKey("Jidkdp1mWL1tRyK=")
                    .setKeyIv("Jidkdp1mWL1jijK=")
    );

    String raw = "这是一个AES的加密解密测试！Abc123,!@#$%^&*()_+,！@#￥%%……&*（）";

    @Test
    void testEncrypt() {
        System.out.println(encryptStrategy.encrypt(raw));
    }

    @Test
    void testDecrypt() {
        String encrypt = encryptStrategy.encrypt(raw);
        System.out.println(encryptStrategy.decrypt(encrypt));
    }
}
