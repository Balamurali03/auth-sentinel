package io.github.balamurali03.auth_sentinel_core.exception;

/**
 * Runtime exception for JWT and core security failures in AuthSentinel.
 */
public class CosmoSecurityException extends RuntimeException {

    public CosmoSecurityException(String message) {
        super(message);
    }

    public CosmoSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
