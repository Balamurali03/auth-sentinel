package io.github.balamurali03.auth_sentinel_annotations.authorization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuthContext}.
 */
class AuthContextTest {

    @BeforeEach
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("isAuthenticated() returns false when context is empty")
    void notAuthenticatedWhenEmpty() {
        assertThat(AuthContext.isAuthenticated()).isFalse();
    }

    @Test
    @DisplayName("isAuthenticated() returns false for anonymous token")
    void notAuthenticatedForAnonymous() {
        AnonymousAuthenticationToken anon = new AnonymousAuthenticationToken(
                "key", "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anon);

        assertThat(AuthContext.isAuthenticated()).isFalse();
    }

    @Test
    @DisplayName("isAuthenticated() returns true for a real user")
    void authenticatedForRealUser() {
        setAuthentication("ROLE_USER");
        assertThat(AuthContext.isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("hasRole() returns true when role is present")
    void hasRolePresent() {
        setAuthentication("ROLE_ADMIN");
        assertThat(AuthContext.hasRole("ROLE_ADMIN")).isTrue();
    }

    @Test
    @DisplayName("hasRole() returns false when role is absent")
    void hasRoleAbsent() {
        setAuthentication("ROLE_USER");
        assertThat(AuthContext.hasRole("ROLE_ADMIN")).isFalse();
    }

    @Test
    @DisplayName("hasRole() returns false when context is empty")
    void hasRoleEmptyContext() {
        assertThat(AuthContext.hasRole("ROLE_USER")).isFalse();
    }

    @Test
    @DisplayName("getAuthentication() returns null when context is empty")
    void getAuthenticationNull() {
        assertThat(AuthContext.getAuthentication()).isNull();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private void setAuthentication(String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                "user", "pass",
                List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
