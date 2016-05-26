package com.github.minly.multitenancy.exception;

/**
 * Created by Jannik on 21.03.15.
 */
public class TenantNotResolveableException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TenantNotResolveableException() {
        super();
    }

    public TenantNotResolveableException(String message) {
        super(message);
    }

    public TenantNotResolveableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TenantNotResolveableException(Throwable cause) {
        super(cause);
    }

    public TenantNotResolveableException(String message,
                                         Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
