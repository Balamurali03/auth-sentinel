package io.github.balamurali03.auth_sentinel_core.exception;

public class CosmoSecurityException extends RuntimeException {

    public CosmoSecurityException(String message) {
        super(message);
    }

    public CosmoSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
