package io.github.balamurali03.auth_sentinel_autoconfigure.resolver;

import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.AuthStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Iterates over all registered {@link AuthStrategy} beans and delegates
 * to the first one that {@linkplain AuthStrategy#supports(HttpServletRequest) supports}
 * the incoming request.
 *
 * <p>Returns {@code null} when no strategy matches (unauthenticated request).
 */
public class AuthStrategyResolver {

    private final List<AuthStrategy> strategies;

    public AuthStrategyResolver(List<AuthStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Resolves an {@link Authentication} for the given request, or {@code null}.
     */
    public Authentication resolve(HttpServletRequest request) {
        for (AuthStrategy strategy : strategies) {
            if (strategy.supports(request)) {
                return strategy.authenticate(request);
            }
        }
        return null;
    }
}
