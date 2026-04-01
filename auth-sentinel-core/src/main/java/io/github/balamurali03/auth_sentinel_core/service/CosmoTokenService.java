package io.github.balamurali03.auth_sentinel_core.service;

import java.util.Map;

/**
 * Contract for JWT generation and validation.
 */
public interface CosmoTokenService {

    /**
     * Generates a signed JWT with the given subject and default expiry.
     */
    String generateToken(String subject);

    /**
     * Generates a signed JWT with the given subject and issuer.
     */
    String generateToken(String subject, String issuer);

    /**
     * Generates a signed JWT with subject, issuer and additional claims.
     */
    String generateToken(String subject, String issuer, Map<String, Object> claims);

    /**
     * Validates the token signature and expiry.
     *
     * @throws io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException
     *         if the token is invalid or expired
     */
    boolean validateToken(String token);

    /**
     * Extracts the {@code sub} claim from a valid token.
     */
    String extractSubject(String token);
}
