package io.github.weasleyj.mybatis.encrypt.interceptor;

import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import io.github.weasleyj.mybatis.encrypt.annotation.Encryption;
import io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptConfigurer;
import io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties;
import io.github.weasleyj.mybatis.encrypt.core.EncryptStrategy;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Default mybatis encrypt interceptor
 *
 * @author weasley
 * @version 1.0.0
 */
@Component
@ConditionalOnBean(annotation = {EnableMybatisEncryption.class})
@Intercepts(value = {
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class DefaultMybatisEncryptInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MybatisEncryptProperties mybatisEncryptProperties;

    public DefaultMybatisEncryptInterceptor(MybatisEncryptProperties mybatisEncryptProperties) {
        this.mybatisEncryptProperties = mybatisEncryptProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (Boolean.FALSE.equals(mybatisEncryptProperties.getEnable())) return invocation.proceed();

        Object[] args = invocation.getArgs();
        Object target = invocation.getTarget();
        Executor executor = target instanceof Executor ? ((Executor) target) : null;
        StatementHandler statementHandler = target instanceof StatementHandler ? ((StatementHandler) target) : null;
        MappedStatement ms = args[0] instanceof MappedStatement ? ((MappedStatement) args[0]) : null;

        if (null != ms) {
            switch (ms.getSqlCommandType()) {
                case INSERT:
                case UPDATE:
                    this.processInsertOrUpdateCommandType(invocation);
                    break;
                case SELECT:
                    return this.processSelectCommandType(invocation);
                default:
                    break;
            }
        }

        return invocation.proceed();
    }

    /**
     * Process insert or update sql command type
     *
     * @param invocation The invocation of mybatis
     * @throws IllegalAccessException The illegal access exception
     */
    public void processInsertOrUpdateCommandType(Invocation invocation) throws IllegalAccessException {
        Object parameter = invocation.getArgs()[1];
        Class<?> clazz = parameter.getClass();
        if (clazz != Object.class) {
            List<Field> encryptFields = FieldUtils.getFieldsListWithAnnotation(clazz, Encryption.class);
            if (CollectionUtils.isEmpty(encryptFields)) return;
            for (Field field : encryptFields) {
                Object plaintextFieldValue = FieldUtils.readField(field, parameter, true);
                if (null == plaintextFieldValue) continue;
                String encrypt = this.deduceEncryptStrategy().encrypt(plaintextFieldValue);
                FieldUtils.writeField(field, parameter, encrypt, true);
                if (logger.isDebugEnabled()) {
                    logger.debug("Encrypt field for '{}', Plaintext: {}, Ciphertext: {}", field.getName(), plaintextFieldValue, encrypt);
                }
            }
        }
    }

    /**
     * Process elect sql command type
     *
     * @param invocation The invocation of mybatis
     * @return The result of proceed
     * @throws IllegalAccessException    The illegal access exception
     * @throws InvocationTargetException The invocation target exception
     */
    public Object processSelectCommandType(Invocation invocation) throws IllegalAccessException, InvocationTargetException {
        Object proceed = invocation.proceed();
        if (proceed instanceof Collection) {
            Collection<?> proceedResults = (Collection<?>) proceed;
            if (CollectionUtils.isEmpty(proceedResults)) return proceed;
            for (Object obj : proceedResults) {
                if (null == obj) continue;
                List<Field> decryptFields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), Encryption.class);
                if (!CollectionUtils.isEmpty(decryptFields)) {
                    for (Field field : decryptFields) {
                        Object ciphertextFieldValue = FieldUtils.readField(field, obj, true);
                        if (null == ciphertextFieldValue) continue;
                        String decrypt = this.deduceEncryptStrategy().decrypt(ciphertextFieldValue);
                        FieldUtils.writeField(field, obj, decrypt, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Decrypt field for '{}', Ciphertext: {}, Plaintext: {}", field.getName(), ciphertextFieldValue, decrypt);
                        }
                    }
                }
            }
        }
        if (proceed instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) proceed;
        }
        return proceed;
    }

    /**
     * Deduce encrypt strategy client
     *
     * @return The instance of {@link EncryptStrategy}
     */
    public EncryptStrategy deduceEncryptStrategy() {
        return MybatisEncryptConfigurer.STRATEGIES_CLIENTS.get(mybatisEncryptProperties.getEncryptType());
    }

}
