package io.github.balamurali03.auth_sentinel_annotations.authorization;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthContext {

    private AuthContext() {}

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    public static boolean hasRole(String role) {
        Authentication auth = getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
