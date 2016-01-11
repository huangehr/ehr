package com.yihu.ehr.address.service;

/**
 * Created by zqb on 2015/6/30.
 */
public class AddressException extends RuntimeException {
    /**
     * Constructs a OrganizationException using the given exception message.
     *
     * @param message The message explaining the reason for the exception
     */
    public AddressException(String message) {
        super( message );
    }

    /**
     * Constructs a OrganizationException using the given message and underlying cause.
     *
     * @param cause The underlying cause.
     */
    public AddressException(Throwable cause) {
        super( cause );
    }

    /**
     * Constructs a OrganizationException using the given message and underlying cause.
     *
     * @param message The message explaining the reason for the exception.
     * @param cause The underlying cause.
     */
    public AddressException(String message, Throwable cause) {
        super( message, cause );
    }
}
