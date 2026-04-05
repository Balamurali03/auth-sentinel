package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import io.github.balamurali03.auth_sentinel_autoconfigure.model.CosmoPrincipal;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authenticates requests that carry an {@code Authorization: Bearer <token>} header.
 */
public class JwtAuthStrategy implements AuthStrategy {

    private final CosmoTokenService tokenService;

    public JwtAuthStrategy(CosmoTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean supports(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ");
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {

        String token   = request.getHeader("Authorization").substring(7);
        tokenService.validateToken(token);           // throws CosmoSecurityException on failure
        String subject = tokenService.extractSubject(token);

        // Dynamically extract whatever roles the developer put in the token
    List<SimpleGrantedAuthority> authorities = tokenService.extractRoles(token)
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        CosmoPrincipal principal = new CosmoPrincipal(
                subject,
                subject,
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }
}
