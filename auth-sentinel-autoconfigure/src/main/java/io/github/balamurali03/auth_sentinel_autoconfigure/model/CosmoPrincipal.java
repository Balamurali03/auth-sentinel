package io.github.balamurali03.auth_sentinel_autoconfigure.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Spring Security {@link org.springframework.security.core.userdetails.UserDetails}
 * implementation that also carries an application-level user identifier.
 */
public class CosmoPrincipal extends User {

    private final String userId;

    public CosmoPrincipal(String userId,
                          String username,
                          String password,
                          Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    /** Returns the application-level user identifier (e.g. UUID or database PK). */
    public String getUserId() {
        return userId;
    }
}
