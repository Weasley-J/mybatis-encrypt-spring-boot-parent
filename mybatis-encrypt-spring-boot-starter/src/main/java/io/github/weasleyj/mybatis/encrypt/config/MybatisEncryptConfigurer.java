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
import static io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties.DiyProperties;
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
@ConfigurationPropertiesScan(basePackages = {"io.github.weasleyj.mybatis.encrypt.config"})
@EnableConfigurationProperties({MybatisEncryptProperties.class, AesProperties.class, DiyProperties.class})
public class MybatisEncryptConfigurer implements InitializingBean {
    /**
     * The clients of encrypt strategies
     */
    public static final Map<EncryptType, EncryptStrategy> STRATEGY_CLIENTS = new ConcurrentHashMap<>(6);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DiyProperties diyProperties;
    private final AesProperties aesProperties;
    /**
     * The list of 'SqlSessionFactory'
     */
    private final List<SqlSessionFactory> sqlSessionFactories;
    private final DefaultMybatisEncryptInterceptor defaultMybatisEncryptInterceptor;

    public MybatisEncryptConfigurer(DiyProperties diyProperties, AesProperties aesProperties, List<SqlSessionFactory> sqlSessionFactories, DefaultMybatisEncryptInterceptor defaultMybatisEncryptInterceptor) {
        this.diyProperties = diyProperties;
        this.aesProperties = aesProperties;
        this.sqlSessionFactories = sqlSessionFactories;
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
        if (null != diyProperties.getEncryptStrategy()) {
            STRATEGY_CLIENTS.put(DIY, ClassUtils.newInstance(diyProperties.getEncryptStrategy()));
        }
        STRATEGY_CLIENTS.put(BASE64, new DefaultBase64EncryptStrategyImpl());
        STRATEGY_CLIENTS.put(AES, new DefaultAesEncryptStrategyImpl(aesProperties));
    }
}
