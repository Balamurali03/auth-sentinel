package io.github.balamurali03.auth_sentinel_autoconfigure.model;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CosmoPrincipal extends User {

    private final String userId;

    public CosmoPrincipal(String userId,
                          String username,
                          String password,
                          Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
