package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link GatewayAuthStrategy}.
 */
class GatewayAuthStrategyTest {

    private GatewayAuthStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new GatewayAuthStrategy();
    }

    @Test
    @DisplayName("supports() returns true when X-Internal-Call: true")
    void supportsInternalCall() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Call", "true");
        assertThat(strategy.supports(request)).isTrue();
    }

    @Test
    @DisplayName("supports() is case-insensitive for header value")
    void supportsCaseInsensitive() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Call", "TRUE");
        assertThat(strategy.supports(request)).isTrue();
    }

    @Test
    @DisplayName("supports() returns false without header")
    void notSupportsWithoutHeader() {
        assertThat(strategy.supports(new MockHttpServletRequest())).isFalse();
    }

    @Test
    @DisplayName("authenticate() builds authentication from gateway headers")
    void authenticateWithRoles() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Call", "true");
        request.addHeader("X-User-Id",    "user-99");
        request.addHeader("X-User-Roles", "ROLE_ADMIN,ROLE_USER");

        Authentication auth = strategy.authenticate(request);

        assertThat(auth).isNotNull();
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getAuthorities())
                .extracting(a -> a.getAuthority())
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    @DisplayName("authenticate() succeeds without roles header (empty authorities)")
    void authenticateNoRoles() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Call", "true");
        request.addHeader("X-User-Id", "user-99");

        Authentication auth = strategy.authenticate(request);

        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("authenticate() throws when X-User-Id is missing")
    void authenticateMissingUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Internal-Call", "true");

        assertThatThrownBy(() -> strategy.authenticate(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("X-User-Id");
    }
}
