package io.github.balamurali03.auth_sentinel_core.service;

import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Default HS256/384/512-based implementation of {@link CosmoTokenService}.
 *
 * <p>RS256 support can be added in a future release by extending
 * {@link #buildSigningKey(String)} to handle asymmetric keys.
 */
public class CosmoTokenServiceImpl implements CosmoTokenService {

    private final Long expiration;
    private final String algorithm;
    private final Key signingKey;

    public CosmoTokenServiceImpl(String secret, Long expiration, String algorithm) {

        if (secret == null || secret.isBlank()) {
            throw new CosmoSecurityException(
                    "cosmo.security.jwt.secret must be configured");
        }
        if (expiration == null || expiration <= 0) {
            throw new CosmoSecurityException(
                    "cosmo.security.jwt.expiration must be a positive number of milliseconds");
        }

        this.expiration = expiration;
        this.algorithm  = (algorithm != null && !algorithm.isBlank()) ? algorithm : "HS256";
        this.signingKey = buildSigningKey(secret);
    }

    // ── Key construction ────────────────────────────────────────────────────

    private Key buildSigningKey(String secret) {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        int actualBits = keyBytes.length * 8;
        int requiredBits = switch (algorithm.toUpperCase()) {
        case "HS384" -> 384;
        case "HS512" -> 512;
        default -> 256;
    };
        if (actualBits < requiredBits) {
    throw new CosmoSecurityException("Weak JWT secret");
}
        if ("HS256".equalsIgnoreCase(algorithm)
                || "HS384".equalsIgnoreCase(algorithm)
                || "HS512".equalsIgnoreCase(algorithm)) {
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        throw new CosmoSecurityException("Unsupported JWT algorithm: " + algorithm
                + ". Supported: HS256, HS384, HS512");
    }

    private SignatureAlgorithm resolveAlgorithm() {
        return switch (algorithm.toUpperCase()) {
            case "HS384" -> SignatureAlgorithm.HS384;
            case "HS512" -> SignatureAlgorithm.HS512;
            default      -> SignatureAlgorithm.HS256;
        };
    }

    // ── CosmoTokenService ───────────────────────────────────────────────────

    @Override
    public String generateToken(String subject) {
        return generateToken(subject, null, null);
    }

    @Override
    public String generateToken(String subject, String issuer) {
        return generateToken(subject, issuer, null);
    }

    @Override
    public String generateToken(String subject,
                                String issuer,
                                Map<String, Object> claims) {

        if (subject == null || subject.isBlank()) {
            throw new CosmoSecurityException("JWT subject must not be blank");
        }

        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, resolveAlgorithm());

        if (issuer != null && !issuer.isBlank()) {
            builder.setIssuer(issuer);
        }
        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }

        return builder.compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CosmoSecurityException("Invalid or expired JWT token", e);
        }
    }

    @Override
    public String extractSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
