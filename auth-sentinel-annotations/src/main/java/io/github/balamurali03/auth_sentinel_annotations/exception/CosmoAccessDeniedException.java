package io.github.balamurali03.auth_sentinel_annotations.exception;



import org.springframework.security.access.AccessDeniedException;

public class CosmoAccessDeniedException extends AccessDeniedException {

    public CosmoAccessDeniedException(String message) {
        super(message);
    }
}
