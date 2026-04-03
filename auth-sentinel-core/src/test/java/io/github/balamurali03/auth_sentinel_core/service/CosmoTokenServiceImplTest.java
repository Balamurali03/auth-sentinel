package io.github.balamurali03.auth_sentinel_core.service;

import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link CosmoTokenServiceImpl}.
 */
class CosmoTokenServiceImplTest {

    // 32+ character secret required for HS256
    private static final String SECRET     = "super-secret-key-that-is-long-enough-for-hs256";
    private static final long   EXPIRATION = 3_600_000L; // 1 hour

    private CosmoTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new CosmoTokenServiceImpl(SECRET, EXPIRATION, "HS256");
    }

    // ── Construction ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Constructor validation")
    class ConstructorValidation {

        @Test
        @DisplayName("throws when secret is null")
        void nullSecret() {
            assertThatThrownBy(() -> new CosmoTokenServiceImpl(null, EXPIRATION, "HS256"))
                    .isInstanceOf(CosmoSecurityException.class)
                    .hasMessageContaining("secret");
        }

        @Test
        @DisplayName("throws when secret is blank")
        void blankSecret() {
            assertThatThrownBy(() -> new CosmoTokenServiceImpl("   ", EXPIRATION, "HS256"))
                    .isInstanceOf(CosmoSecurityException.class);
        }

        @Test
        @DisplayName("throws when expiration is null")
        void nullExpiration() {
            assertThatThrownBy(() -> new CosmoTokenServiceImpl(SECRET, null, "HS256"))
                    .isInstanceOf(CosmoSecurityException.class)
                    .hasMessageContaining("expiration");
        }

        @Test
        @DisplayName("throws when expiration is zero")
        void zeroExpiration() {
            assertThatThrownBy(() -> new CosmoTokenServiceImpl(SECRET, 0L, "HS256"))
                    .isInstanceOf(CosmoSecurityException.class);
        }

        @Test
        @DisplayName("throws for unsupported algorithm")
        void unsupportedAlgorithm() {
            assertThatThrownBy(() -> new CosmoTokenServiceImpl(SECRET, EXPIRATION, "RS256"))
                    .isInstanceOf(CosmoSecurityException.class)
                    .hasMessageContaining("Unsupported JWT algorithm");
        }

        @Test
        @DisplayName("defaults to HS256 when algorithm is null")
        void defaultAlgorithm() {
            assertThatNoException().isThrownBy(
                    () -> new CosmoTokenServiceImpl(SECRET, EXPIRATION, null));
        }
    }

    // ── Token generation ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Token generation")
    class TokenGeneration {

        @Test
        @DisplayName("generates non-blank token for valid subject")
        void generateTokenSubjectOnly() {
            String token = tokenService.generateToken("user-123");
            assertThat(token).isNotBlank();
            // JWT has three base64url segments separated by dots
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("generates token with issuer")
        void generateTokenWithIssuer() {
            String token = tokenService.generateToken("user-123", "my-service");
            assertThat(token).isNotBlank();
        }

        @Test
        @DisplayName("generates token with extra claims")
        void generateTokenWithClaims() {
            Map<String, Object> claims = Map.of("role", "ADMIN", "tenantId", "42");
            String token = tokenService.generateToken("user-123", "my-service", claims);
            assertThat(token).isNotBlank();
        }

        @Test
        @DisplayName("throws when subject is null")
        void nullSubject() {
            assertThatThrownBy(() -> tokenService.generateToken(null))
                    .isInstanceOf(CosmoSecurityException.class)
                    .hasMessageContaining("subject");
        }

        @Test
        @DisplayName("throws when subject is blank")
        void blankSubject() {
            assertThatThrownBy(() -> tokenService.generateToken("  "))
                    .isInstanceOf(CosmoSecurityException.class);
        }
    }

    // ── Validation & extraction ─────────────────────────────────────────────

    @Nested
    @DisplayName("Token validation and subject extraction")
    class ValidationAndExtraction {

        @Test
        @DisplayName("validates a freshly generated token")
        void validateFreshToken() {
            String token = tokenService.generateToken("alice");
            assertThat(tokenService.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("extracts the correct subject")
        void extractSubject() {
            String token = tokenService.generateToken("bob");
            assertThat(tokenService.extractSubject(token)).isEqualTo("bob");
        }

        @Test
        @DisplayName("throws for a tampered token")
        void tamperedToken() {
            String token   = tokenService.generateToken("charlie");
            String tampered = token.substring(0, token.length() - 4) + "XXXX";
            assertThatThrownBy(() -> tokenService.validateToken(tampered))
                    .isInstanceOf(CosmoSecurityException.class);
        }

        @Test
        @DisplayName("throws for a completely invalid token")
        void invalidToken() {
            assertThatThrownBy(() -> tokenService.validateToken("not.a.token"))
                    .isInstanceOf(CosmoSecurityException.class);
        }

        @Test
        @DisplayName("throws for an expired token")
        void expiredToken() {
            CosmoTokenService shortLived =
                    new CosmoTokenServiceImpl(SECRET, 1L, "HS256"); // 1 ms
            String token = shortLived.generateToken("dave");

            // spin a tiny bit to let it expire
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}

            assertThatThrownBy(() -> shortLived.validateToken(token))
                    .isInstanceOf(CosmoSecurityException.class);
        }
    }

    // ── Algorithm variants ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Algorithm variants")
    class AlgorithmVariants {

        @Test
        @DisplayName("HS384 round-trip")
        void hs384() {
            CosmoTokenService svc = new CosmoTokenServiceImpl(SECRET, EXPIRATION, "HS384");
            String token = svc.generateToken("hs384-user");
            assertThat(svc.validateToken(token)).isTrue();
            assertThat(svc.extractSubject(token)).isEqualTo("hs384-user");
        }

        @Test
        @DisplayName("HS512 round-trip")
        void hs512() {
            CosmoTokenService svc = new CosmoTokenServiceImpl(SECRET, EXPIRATION, "HS512");
            String token = svc.generateToken("hs512-user");
            assertThat(svc.validateToken(token)).isTrue();
            assertThat(svc.extractSubject(token)).isEqualTo("hs512-user");
        }
    }
}
