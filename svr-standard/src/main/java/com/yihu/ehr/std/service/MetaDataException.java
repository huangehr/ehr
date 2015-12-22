package com.yihu.ehr.std.service;


/**
 * 数据元异常.
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:06
 */
public class MetaDataException extends RuntimeException {

    /**
     * Constructs a MetaDataException using the given exception message.
     *
     * @param message The message explaining the reason for the exception
     */
    public MetaDataException(String message) {
        super( message );
    }

    /**
     * Constructs a MetaDataException using the given message and underlying cause.
     *
     * @param cause The underlying cause.
     */
    public MetaDataException(Throwable cause) {
        super( cause );
    }

    /**
     * Constructs a MetaDataException using the given message and underlying cause.
     *
     * @param message The message explaining the reason for the exception.
     * @param cause The underlying cause.
     */
    public MetaDataException(String message, Throwable cause) {
        super( message, cause );
    }

}