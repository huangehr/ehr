package com.yihu.ehr.std.service;


/**
 * 数据集异常
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:05
 */
public class DataSetException extends RuntimeException {

    /**
     * Constructs a DataSetException using the given exception message.
     *
     * @param message The message explaining the reason for the exception
     */
    public DataSetException(String message) {
        super( message );
    }

    /**
     * Constructs a DataSetException using the given message and underlying cause.
     *
     * @param cause The underlying cause.
     */
    public DataSetException(Throwable cause) {
        super( cause );
    }

    /**
     * Constructs a DataSetException using the given message and underlying cause.
     *
     * @param message The message explaining the reason for the exception.
     * @param cause The underlying cause.
     */
    public DataSetException(String message, Throwable cause) {
        super( message, cause );
    }

}