package io.github.balamurali03.auth_sentinel_autoconfigure.resolver;

import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.AuthStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuthStrategyResolver}.
 */
class AuthStrategyResolverTest {

    @Test
    @DisplayName("returns null when no strategy supports the request")
    void noMatch() {
        AuthStrategy neverSupports = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return false; }
            @Override public Authentication authenticate(HttpServletRequest r) { return null; }
        };

        AuthStrategyResolver resolver = new AuthStrategyResolver(List.of(neverSupports));
        assertThat(resolver.resolve(new MockHttpServletRequest())).isNull();
    }

    @Test
    @DisplayName("returns authentication from first matching strategy")
    void firstMatchWins() {
        Authentication expected = new UsernamePasswordAuthenticationToken(
                "user", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        AuthStrategy first = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) { return expected; }
        };

        AuthStrategy second = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) {
                throw new AssertionError("Second strategy should not be called");
            }
        };

        AuthStrategyResolver resolver = new AuthStrategyResolver(List.of(first, second));
        assertThat(resolver.resolve(new MockHttpServletRequest())).isSameAs(expected);
    }

    @Test
    @DisplayName("skips non-matching strategies and delegates to matching one")
    void skipsThenMatches() {
        Authentication expected = new UsernamePasswordAuthenticationToken(
                "user", null,
                List.of(new SimpleGrantedAuthority("ROLE_CERT_USER")));

        AuthStrategy skip = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return false; }
            @Override public Authentication authenticate(HttpServletRequest r) { return null; }
        };

        AuthStrategy match = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) { return expected; }
        };

        AuthStrategyResolver resolver = new AuthStrategyResolver(List.of(skip, match));
        assertThat(resolver.resolve(new MockHttpServletRequest())).isSameAs(expected);
    }

    @Test
    @DisplayName("returns null when strategy list is empty")
    void emptyList() {
        AuthStrategyResolver resolver = new AuthStrategyResolver(List.of());
        assertThat(resolver.resolve(new MockHttpServletRequest())).isNull();
    }
}
