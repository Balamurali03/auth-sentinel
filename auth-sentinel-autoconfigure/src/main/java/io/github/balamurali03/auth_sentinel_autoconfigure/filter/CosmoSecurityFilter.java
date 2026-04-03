package io.github.balamurali03.auth_sentinel_autoconfigure.filter;

import io.github.balamurali03.auth_sentinel_autoconfigure.resolver.AuthStrategyResolver;
import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that authenticates incoming requests via the
 * {@link AuthStrategyResolver} and stores the result in the
 * {@link SecurityContextHolder}.
 *
 * <p>If the chosen strategy throws a {@link CosmoSecurityException}
 * (e.g. expired or malformed JWT), the filter responds immediately
 * with {@code 401 Unauthorized} rather than propagating the exception.
 *
 * <p>Registration: Spring Boot auto-registers {@link OncePerRequestFilter}
 * beans, so no explicit {@code FilterRegistrationBean} is needed. If you
 * need to control the order or URL pattern, define your own
 * {@code FilterRegistrationBean<CosmoSecurityFilter>} bean.
 */
public class CosmoSecurityFilter extends OncePerRequestFilter {

    private final AuthStrategyResolver resolver;

    public CosmoSecurityFilter(AuthStrategyResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         chain)
            throws ServletException, IOException {

        try {
            SecurityContextHolder.clearContext();
            Authentication auth = resolver.resolve(request);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CosmoSecurityException ex) {
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, ex.getMessage());
            return;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            sendUnauthorized(response, "Authentication failed");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"message\":\"" + escape(message) + "\"}");
    }

    /** Minimal JSON-safe escaping for the error message. */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
