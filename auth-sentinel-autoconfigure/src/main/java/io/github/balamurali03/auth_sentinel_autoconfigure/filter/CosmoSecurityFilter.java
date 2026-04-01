package io.github.balamurali03.auth_sentinel_autoconfigure.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.balamurali03.auth_sentinel_autoconfigure.resolver.AuthStrategyResolver;

import java.io.IOException;

public class CosmoSecurityFilter extends OncePerRequestFilter {

    private final AuthStrategyResolver resolver;

    public CosmoSecurityFilter(AuthStrategyResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = resolver.resolve(request);

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
