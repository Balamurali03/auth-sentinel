package io.github.balamurali03.auth_sentinel_annotations.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * Thrown by {@code CosmoSecurityAspect} when the caller lacks
 * the required authentication or role.
 */
public class CosmoAccessDeniedException extends AccessDeniedException {

    public CosmoAccessDeniedException(String message) {
        super(message);
    }
}
