package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

// import io.cosmocoder.security.core.service.CosmoTokenService;
// import io.cosmocoder.security.autoconfig.model.CosmoPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.github.balamurali03.auth_sentinel_autoconfigure.model.CosmoPrincipal;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenService;

import java.util.List;

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

        String token = request.getHeader("Authorization").substring(7);

        tokenService.validateToken(token);

        String subject = tokenService.extractSubject(token);

        CosmoPrincipal principal =
                new CosmoPrincipal(subject,
                        subject,
                        "",
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }
}
