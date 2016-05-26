package com.github.minly.multitenancy.exception;

/**
 * Created by Jannik on 21.03.15.
 */
public class TenantDataSourceNotResolvableException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TenantDataSourceNotResolvableException() {
        super();
    }

    public TenantDataSourceNotResolvableException(String message) {
        super(message);
    }

    public TenantDataSourceNotResolvableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TenantDataSourceNotResolvableException(Throwable cause) {
        super(cause);
    }

    public TenantDataSourceNotResolvableException(String message,
                                                  Throwable cause,
                                                  boolean enableSuppression,
                                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
