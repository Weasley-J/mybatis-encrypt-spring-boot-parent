package io.github.weasleyj.mybatis.encrypt.annotation;

import io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptConfigurer;
import io.github.weasleyj.mybatis.encrypt.core.DefaultAesEncryptStrategyImpl;
import io.github.weasleyj.mybatis.encrypt.core.DefaultBase64EncryptStrategyImpl;
import io.github.weasleyj.mybatis.encrypt.interceptor.DefaultMybatisEncryptInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To Enable autoconfiguration for Mybatis Encryption
 *
 * @author weasley
 * @version 1.0.0
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MybatisEncryptConfigurer.class, DefaultMybatisEncryptInterceptor.class, DefaultAesEncryptStrategyImpl.class, DefaultBase64EncryptStrategyImpl.class,})
public @interface EnableMybatisEncryption {
}
