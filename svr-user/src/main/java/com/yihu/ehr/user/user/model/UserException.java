package com.yihu.ehr.user.user.model;

/**
 * Created by Sand on 2015/5/29.
 */
public class UserException extends RuntimeException {
    /**
     * Constructs a UserException using the given exception message.
     *
     * @param message The message explaining the reason for the exception
     */
    public UserException(String message) {
        super( message );
    }

    /**
     * Constructs a UserException using the given message and underlying cause.
     *
     * @param cause The underlying cause.
     */
    public UserException(Throwable cause) {
        super( cause );
    }

    /**
     * Constructs a UserException using the given message and underlying cause.
     *
     * @param message The message explaining the reason for the exception.
     * @param cause The underlying cause.
     */
    public UserException(String message, Throwable cause) {
        super( message, cause );
    }
}
