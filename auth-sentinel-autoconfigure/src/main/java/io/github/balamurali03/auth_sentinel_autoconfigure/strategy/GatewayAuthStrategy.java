package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import io.github.balamurali03.auth_sentinel_autoconfigure.model.CosmoPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Trusts requests that were pre-authenticated by an internal API gateway.
 *
 * <p>Expected headers:
 * <ul>
 *   <li>{@code X-Internal-Call: true}  – signals a gateway-forwarded request</li>
 *   <li>{@code X-User-Id}              – the authenticated user's identifier</li>
 *   <li>{@code X-User-Roles}           – comma-separated list of Spring Security role names</li>
 * </ul>
 *
 * <p><strong>Security note:</strong> Only enable this strategy behind a trusted
 * network boundary. Any caller that can set these headers will be authenticated.
 */
public class GatewayAuthStrategy implements AuthStrategy {

    @Override
    public boolean supports(HttpServletRequest request) {
        return "true".equalsIgnoreCase(request.getHeader("X-Internal-Call"));
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        String roles  = request.getHeader("X-User-Roles");

        if (!StringUtils.hasText(userId)) {
            throw new IllegalStateException(
                    "Gateway request is missing required X-User-Id header");
        }

        List<SimpleGrantedAuthority> authorities = StringUtils.hasText(roles)
                ? Arrays.stream(roles.split(","))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        CosmoPrincipal principal = new CosmoPrincipal(userId, userId, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
