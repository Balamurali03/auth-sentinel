package io.github.balamurali03.auth_sentinel_autoconfigure.resolver;

import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.AuthStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public class AuthStrategyResolver {

    private final List<AuthStrategy> strategies;

    public AuthStrategyResolver(List<AuthStrategy> strategies) {
        this.strategies = strategies;
    }

    public Authentication resolve(HttpServletRequest request) {

        for (AuthStrategy strategy : strategies) {
            if (strategy.supports(request)) {
                return strategy.authenticate(request);
            }
        }

        return null;
    }
}
