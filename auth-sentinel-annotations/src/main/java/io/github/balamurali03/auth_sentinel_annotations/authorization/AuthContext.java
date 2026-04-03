package io.github.balamurali03.auth_sentinel_annotations.authorization;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience facade over {@link SecurityContextHolder}.
 */
public final class AuthContext {

    private AuthContext() {}

    /**
     * Returns the current {@link Authentication}, or {@code null} if none is set.
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Returns {@code true} when a non-anonymous, authenticated principal is present.
     *
     * <p>Note: Spring sets an {@link AnonymousAuthenticationToken} for unauthenticated
     * requests, so we must explicitly exclude it here.
     */
    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

    /**
     * Returns {@code true} when the current principal holds the given role.
     *
     * @param role  Spring Security role name, e.g. {@code "ROLE_ADMIN"}
     */
    public static boolean hasRole(String role) {
        Authentication auth = getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

   /**
 * Returns the current principal object (e.g. UserDetails, JWT principal, etc.)
 */
public static Object getPrincipal() {
    Authentication auth = getAuthentication();
    return (auth != null) ? auth.getPrincipal() : null;
}
}
