package io.github.weasleyj.mybatis.encrypt.constant;

import java.math.BigDecimal;
import java.util.*;

/**
 * The encryption constant
 *
 * @author weasley
 * @version 1.0.0
 */
public interface EncryptConstant {
    /**
     * SQL start with insert
     */
    String INSERT = "INSERT";
    /**
     * SQL start with update
     */
    String UPDATE = "UPDATE";
    /**
     * SQL start with select
     */
    String SELECT = "SELECT";

    /**
     * UNDERLYING_DATA_TYPES
     */
    @SuppressWarnings({"all"})
    Set<Class<?>> UNDERLYING_DATA_TYPES = new HashSet<Class<?>>() {{
        add(String.class);
        add(Boolean.class);
        add(Short.class);
        add(Double.class);
        add(Integer.class);
        add(Long.class);
        add(BigDecimal.class);
        add(List.class);
        add(ArrayList.class);
        add(Collection.class);
        add(Map.class);
        add(HashMap.class);
        add(LinkedHashMap.class);
        add(Object.class);
    }};
}
