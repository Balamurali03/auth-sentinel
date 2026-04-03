package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenService;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link JwtAuthStrategy}.
 */
class JwtAuthStrategyTest {

    private static final String SECRET     = "super-secret-key-at-least-32-chars-long!";
    private static final long   EXPIRATION = 3_600_000L;

    private CosmoTokenService  tokenService;
    private JwtAuthStrategy    strategy;

    @BeforeEach
    void setUp() {
        tokenService = new CosmoTokenServiceImpl(SECRET, EXPIRATION, "HS256");
        strategy     = new JwtAuthStrategy(tokenService);
    }

    @Test
    @DisplayName("supports() returns true for Bearer header")
    void supportsBearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer some.token.here");
        assertThat(strategy.supports(request)).isTrue();
    }

    @Test
    @DisplayName("supports() returns false when no Authorization header")
    void supportsNoHeader() {
        assertThat(strategy.supports(new MockHttpServletRequest())).isFalse();
    }

    @Test
    @DisplayName("supports() returns false for Basic auth header")
    void supportsBasicAuth() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        assertThat(strategy.supports(request)).isFalse();
    }

    @Test
    @DisplayName("authenticate() returns populated Authentication for valid token")
    void authenticateValid() {
        String token = tokenService.generateToken("user-42");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        Authentication auth = strategy.authenticate(request);

        assertThat(auth).isNotNull();
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    @DisplayName("authenticate() throws CosmoSecurityException for invalid token")
    void authenticateInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.value");

        assertThatThrownBy(() -> strategy.authenticate(request))
                .isInstanceOf(CosmoSecurityException.class);
    }
}
