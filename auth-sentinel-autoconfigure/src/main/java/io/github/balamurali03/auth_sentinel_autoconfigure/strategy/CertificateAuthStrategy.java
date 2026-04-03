package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import io.github.balamurali03.auth_sentinel_autoconfigure.model.CosmoPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Authenticates requests that present a client-side X.509 certificate.
 *
 * <p>The certificate's RFC 2253 Subject DN is used as both the principal
 * identifier and the username. The granted authority is {@code ROLE_CERT_USER}.
 */
public class CertificateAuthStrategy implements AuthStrategy {

    private static final String CERT_ATTR = "jakarta.servlet.request.X509Certificate";

    @Override
    public boolean supports(HttpServletRequest request) {
        return request.getAttribute(CERT_ATTR) != null;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {

        X509Certificate[] certs =
                (X509Certificate[]) request.getAttribute(CERT_ATTR);

        if (certs == null || certs.length == 0) {
            throw new IllegalStateException("No X.509 certificate found in request");
        }

        String subject = certs[0].getSubjectX500Principal().getName();

        CosmoPrincipal principal = new CosmoPrincipal(
                subject,
                subject,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_CERT_USER"))
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }
}
