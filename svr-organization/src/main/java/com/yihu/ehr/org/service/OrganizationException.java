package com.yihu.ehr.org.service;

/**
 * Created by Sand on 2015/5/29.
 */
public class OrganizationException extends RuntimeException {
    /**
     * Constructs a OrganizationException using the given exception message.
     *
     * @param message The message explaining the reason for the exception
     */
    public OrganizationException(String message) {
        super( message );
    }

    /**
     * Constructs a OrganizationException using the given message and underlying cause.
     *
     * @param cause The underlying cause.
     */
    public OrganizationException(Throwable cause) {
        super( cause );
    }

    /**
     * Constructs a OrganizationException using the given message and underlying cause.
     *
     * @param message The message explaining the reason for the exception.
     * @param cause The underlying cause.
     */
    public OrganizationException(String message, Throwable cause) {
        super( message, cause );
    }
}
