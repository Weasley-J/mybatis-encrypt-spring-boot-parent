package io.github.weasleyj.mybatis.encrypt.config;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import io.github.weasleyj.mybatis.encrypt.constant.EncryptType;
import io.github.weasleyj.mybatis.encrypt.core.DefaultAesEncryptStrategyImpl;
import io.github.weasleyj.mybatis.encrypt.core.DefaultBase64EncryptStrategyImpl;
import io.github.weasleyj.mybatis.encrypt.core.EncryptStrategy;
import io.github.weasleyj.mybatis.encrypt.interceptor.DefaultMybatisEncryptInterceptor;
import lombok.Getter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.objenesis.instantiator.util.ClassUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties.AesProperties;
import static io.github.weasleyj.mybatis.encrypt.constant.EncryptType.AES;
import static io.github.weasleyj.mybatis.encrypt.constant.EncryptType.BASE64;
import static io.github.weasleyj.mybatis.encrypt.constant.EncryptType.DIY;

/**
 * Mybatis Encrypt Configuration
 *
 * @author weasley
 * @version 1.0.0
 */
@Getter
@Component
@Lazy(value = false)
@ConditionalOnClass({SqlSessionFactory.class})
@ConditionalOnBean(annotation = {EnableMybatisEncryption.class})
@EnableConfigurationProperties({MybatisEncryptProperties.class, AesProperties.class})
@ConfigurationPropertiesScan(basePackages = {"io.github.weasleyj.mybatis.encrypt.config"})
public class MybatisEncryptConfiguration implements InitializingBean {
    /**
     * The clients of encrypt strategies
     */
    public static final Map<EncryptType, EncryptStrategy> STRATEGIES_CLIENTS = new ConcurrentHashMap<>(6);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AesProperties aesProperties;
    /**
     * The list of 'SqlSessionFactory'
     */
    private final List<SqlSessionFactory> sqlSessionFactories;
    private final MybatisEncryptProperties mybatisEncryptProperties;
    private final DefaultMybatisEncryptInterceptor defaultMybatisEncryptInterceptor;

    public MybatisEncryptConfiguration(AesProperties aesProperties,
                                       List<SqlSessionFactory> sqlSessionFactories,
                                       MybatisEncryptProperties mybatisEncryptProperties,
                                       DefaultMybatisEncryptInterceptor defaultMybatisEncryptInterceptor) {
        this.aesProperties = aesProperties;
        this.sqlSessionFactories = sqlSessionFactories;
        this.mybatisEncryptProperties = mybatisEncryptProperties;
        this.defaultMybatisEncryptInterceptor = defaultMybatisEncryptInterceptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sqlSessionFactories.forEach(sqlSessionFactory -> {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            if (null != configuration && !configuration.getInterceptors().contains(defaultMybatisEncryptInterceptor)) {
                configuration.addInterceptor(defaultMybatisEncryptInterceptor);
            }
        });
        if (null != mybatisEncryptProperties.getEncryptStrategy()) {
            STRATEGIES_CLIENTS.put(DIY, ClassUtils.newInstance(mybatisEncryptProperties.getEncryptStrategy()));
        }
        STRATEGIES_CLIENTS.put(BASE64, new DefaultBase64EncryptStrategyImpl());
        STRATEGIES_CLIENTS.put(AES, new DefaultAesEncryptStrategyImpl(aesProperties));
    }
}
