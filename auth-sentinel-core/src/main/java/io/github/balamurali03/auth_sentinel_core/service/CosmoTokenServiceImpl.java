package io.github.balamurali03.auth_sentinel_core.service;

import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class CosmoTokenServiceImpl implements CosmoTokenService {

    private final String secret;
    private final Long expiration;
    private final String algorithm;
    private final Key signingKey;

    public CosmoTokenServiceImpl(String secret,
                                  Long expiration,
                                  String algorithm) {

        if (secret == null || secret.isBlank()) {
            throw new CosmoSecurityException("JWT secret must be configured");
        }

        if (expiration == null || expiration <= 0) {
            throw new CosmoSecurityException("JWT expiration must be greater than 0");
        }

        this.secret = secret;
        this.expiration = expiration;
        this.algorithm = algorithm != null ? algorithm : "HS256";
        this.signingKey = buildSigningKey();
    }

    private Key buildSigningKey() {

        if ("HS256".equalsIgnoreCase(algorithm)) {
            return Keys.hmacShaKeyFor(
                    secret.getBytes(StandardCharsets.UTF_8)
            );
        }

        throw new CosmoSecurityException("Unsupported algorithm: " + algorithm);
    }

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
            throw new CosmoSecurityException("JWT subject is mandatory");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256);

        if (issuer != null) {
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
            throw new CosmoSecurityException("Invalid JWT token", e);
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
