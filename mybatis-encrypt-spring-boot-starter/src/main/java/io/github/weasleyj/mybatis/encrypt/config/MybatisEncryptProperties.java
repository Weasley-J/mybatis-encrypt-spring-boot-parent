package io.github.weasleyj.mybatis.encrypt.config;

import io.github.weasleyj.mybatis.encrypt.constant.EncryptType;
import io.github.weasleyj.mybatis.encrypt.core.EncryptStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties.PREFIX;
import static io.github.weasleyj.mybatis.encrypt.constant.EncryptType.BASE64;

/**
 * Mybatis Encrypt Properties
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ConfigurationProperties(prefix = PREFIX)
public class MybatisEncryptProperties {
    /**
     * The prefix of configuration properties
     */
    public static final String PREFIX = "mybatis.encrypt";
    /**
     * enable encryption for mybatis
     */
    private Boolean enable = true;
    /**
     * The type of encryption.
     *
     * @see EncryptType
     */
    private EncryptType encryptType = BASE64;

    /**
     * The configuration properties of AES
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @ConfigurationProperties(prefix = PREFIX + ".aes")
    public static class AesProperties {
        /**
         * The secret key for AES encrypt, the length must be 16.
         */
        private String secretKey;
        /**
         * The offset for secretKey, the length must be 16.
         */
        private String keyIv;

        public String getKeyIv() {
            if (keyIv.length() != 16) {
                throw new IllegalArgumentException("The length of keyIv must be 16: " + keyIv);
            }
            return keyIv;
        }

        public String getSecretKey() {
            if (secretKey.length() != 16) {
                throw new IllegalArgumentException("secret-key length must be 16: " + secretKey);
            }
            return secretKey;
        }
    }

    /**
     * The configuration properties of user DIY encrypt strategy
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @ConfigurationProperties(prefix = PREFIX + ".diy")
    public static class DiyProperties {
        /**
         * Developer defines encryption and decryption policies
         */
        private Class<? extends EncryptStrategy> encryptStrategy = null;
    }
}
