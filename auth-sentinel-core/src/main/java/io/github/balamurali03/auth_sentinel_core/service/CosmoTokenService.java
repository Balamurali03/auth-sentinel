package io.github.balamurali03.auth_sentinel_core.service;

import java.util.Map;

public interface CosmoTokenService {

    String generateToken(String subject);

    String generateToken(String subject, String issuer);

    String generateToken(String subject,
                         String issuer,
                         Map<String, Object> claims);

    boolean validateToken(String token);

    String extractSubject(String token);
}
