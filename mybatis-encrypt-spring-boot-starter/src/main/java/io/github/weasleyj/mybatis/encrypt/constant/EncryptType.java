package io.github.weasleyj.mybatis.encrypt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The encryption type
 *
 * @author weasley
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum EncryptType implements EncryptConstant {
    /**
     * Use AES for encryption and decryption
     */
    AES,
    /**
     * Use BASE64 for encryption and decryption
     */
    BASE64,
    /**
     * User defined encrypt strategy
     */
    DIY
}
