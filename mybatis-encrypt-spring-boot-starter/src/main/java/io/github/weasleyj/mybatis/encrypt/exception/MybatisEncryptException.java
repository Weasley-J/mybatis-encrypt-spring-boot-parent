package io.github.weasleyj.mybatis.encrypt.exception;

/**
 * Mybatis encrypt exception
 *
 * @author weasley
 * @version 1.0.0
 */
public class MybatisEncryptException extends RuntimeException {
    /**
     * The code for mybatis encrypt exception
     */
    private int code = 10089;
    /**
     * The message for mybatis encrypt exception
     */
    private String message;


    public MybatisEncryptException() {
        super();
    }

    public MybatisEncryptException(String message) {
        super(message);
    }

    public MybatisEncryptException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public MybatisEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
