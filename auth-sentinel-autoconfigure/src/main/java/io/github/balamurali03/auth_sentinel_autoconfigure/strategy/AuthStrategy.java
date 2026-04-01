package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

/**
 * Strategy interface for request-level authentication.
 *
 * <p>Implementations are evaluated in order by {@link
 * io.github.balamurali03.auth_sentinel_autoconfigure.resolver.AuthStrategyResolver};
 * the first strategy whose {@link #supports(HttpServletRequest)} returns {@code true}
 * wins.
 */
public interface AuthStrategy {

    /**
     * Returns {@code true} when this strategy can handle the given request.
     */
    boolean supports(HttpServletRequest request);

    /**
     * Authenticates the request and returns a populated {@link Authentication}.
     *
     * @throws io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException
     *         if authentication fails
     */
    Authentication authenticate(HttpServletRequest request);
}
