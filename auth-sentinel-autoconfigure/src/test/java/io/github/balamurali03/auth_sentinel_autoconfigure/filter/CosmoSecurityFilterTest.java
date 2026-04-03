package io.github.balamurali03.auth_sentinel_autoconfigure.filter;

import io.github.balamurali03.auth_sentinel_autoconfigure.resolver.AuthStrategyResolver;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.AuthStrategy;
import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CosmoSecurityFilter}.
 */
class CosmoSecurityFilterTest {

    private MockHttpServletRequest  request;
    private MockHttpServletResponse response;
    private FilterChain             chain;

    @BeforeEach
    void setUp() {
        request  = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain    = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("populates SecurityContext and continues chain when auth resolves")
    void populatesContextAndContinues() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        AuthStrategy strategy = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) { return auth; }
        };

        CosmoSecurityFilter filter = new CosmoSecurityFilter(
                new AuthStrategyResolver(List.of(strategy)));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isSameAs(auth);
    }

    @Test
    @DisplayName("continues chain without setting context when no strategy matches")
    void continuesWhenNoStrategyMatches() throws Exception {
        AuthStrategy strategy = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return false; }
            @Override public Authentication authenticate(HttpServletRequest r) { return null; }
        };

        CosmoSecurityFilter filter = new CosmoSecurityFilter(
                new AuthStrategyResolver(List.of(strategy)));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("responds 401 and halts chain when CosmoSecurityException thrown")
    void returns401OnCosmoSecurityException() throws Exception {
        AuthStrategy strategy = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) {
                throw new CosmoSecurityException("Token expired");
            }
        };

        CosmoSecurityFilter filter = new CosmoSecurityFilter(
                new AuthStrategyResolver(List.of(strategy)));

        filter.doFilterInternal(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).contains("Token expired");
    }

    @Test
    @DisplayName("responds 401 on any unexpected runtime exception")
    void returns401OnGenericException() throws Exception {
        AuthStrategy strategy = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) {
                throw new RuntimeException("unexpected");
            }
        };

        CosmoSecurityFilter filter = new CosmoSecurityFilter(
                new AuthStrategyResolver(List.of(strategy)));

        filter.doFilterInternal(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("JSON response contains valid structure")
    void jsonResponseStructure() throws Exception {
        AuthStrategy strategy = new AuthStrategy() {
            @Override public boolean supports(HttpServletRequest r) { return true; }
            @Override public Authentication authenticate(HttpServletRequest r) {
                throw new CosmoSecurityException("bad token");
            }
        };

        CosmoSecurityFilter filter = new CosmoSecurityFilter(
                new AuthStrategyResolver(List.of(strategy)));

        filter.doFilterInternal(request, response, chain);

        String body = response.getContentAsString();
        assertThat(body).contains("\"error\"");
        assertThat(body).contains("\"message\"");
        assertThat(body).contains("Unauthorized");
    }
}
