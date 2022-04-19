package com.xy.common.domain.exception;

/**
 * @author xiaoye
 * @create 2021-09-28 11:27
 */
public class ForeignKeyNotFoundException extends RuntimeException{
    public ForeignKeyNotFoundException() {
    }

    public ForeignKeyNotFoundException(String message) {
        super(message);
    }

    public ForeignKeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForeignKeyNotFoundException(Throwable cause) {
        super(cause);
    }

    public ForeignKeyNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
