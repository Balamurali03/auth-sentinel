package io.github.balamurali03.auth_sentinel_autoconfigure.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for AuthSentinel JWT support.
 *
 * <pre>
 * cosmo:
 *   security:
 *     jwt:
 *       secret: my-very-long-secret-key-at-least-32-chars
 *       expiration: 3600000   # 1 hour in milliseconds
 *       algorithm: HS256      # HS256 | HS384 | HS512 (default: HS256)
 * </pre>
 */
@Validated
@ConfigurationProperties(prefix = "cosmo.security.jwt")
public class CosmoJwtProperties {

    /** HMAC secret key (required for HS* algorithms). */
    @NotBlank(message = "cosmo.security.jwt.secret must not be blank")
    private String secret;

    /** Token validity in milliseconds. */
    @NotNull
    @Positive(message = "cosmo.security.jwt.expiration must be positive")
    private Long expiration;

    /** Signature algorithm — HS256, HS384 or HS512. Defaults to HS256. */
    private String algorithm = "HS256";

    // ── Getters / Setters ──────────────────────────────────────────────────

    public String getSecret()              { return secret; }
    public void   setSecret(String s)      { this.secret = s; }

    public Long   getExpiration()          { return expiration; }
    public void   setExpiration(Long e)    { this.expiration = e; }

    public String getAlgorithm()           { return algorithm; }
    public void   setAlgorithm(String a)   { this.algorithm = a; }
}
