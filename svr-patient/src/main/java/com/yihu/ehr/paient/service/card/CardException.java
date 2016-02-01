package com.yihu.ehr.paient.service.card;

/**
 * Created by Sand on 2015/5/29.
 */
public class CardException extends RuntimeException {
    /**
     * Constructs a OrganizationException using the given exception message.
     *
     * @param message The message explaining the reason for the exception
     */
    public CardException(String message) {
        super( message );
    }

    /**
     * Constructs a OrganizationException using the given message and underlying cause.
     *
     * @param cause The underlying cause.
     */
    public CardException(Throwable cause) {
        super( cause );
    }

    /**
     * Constructs a OrganizationException using the given message and underlying cause.
     *
     * @param message The message explaining the reason for the exception.
     * @param cause The underlying cause.
     */
    public CardException(String message, Throwable cause) {
        super( message, cause );
    }
}
