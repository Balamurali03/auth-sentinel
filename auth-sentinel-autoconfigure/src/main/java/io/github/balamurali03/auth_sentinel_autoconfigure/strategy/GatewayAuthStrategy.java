package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.model.CosmoPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GatewayAuthStrategy implements AuthStrategy {

    @Override
    public boolean supports(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("X-Internal-Call"));
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        String roles = request.getHeader("X-User-Roles");

        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(roles.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        CosmoPrincipal principal =
                new CosmoPrincipal(userId, userId, "", authorities);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );
    }
}
