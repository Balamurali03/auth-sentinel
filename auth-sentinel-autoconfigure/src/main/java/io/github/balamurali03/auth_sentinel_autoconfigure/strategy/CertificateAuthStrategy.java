package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;

import io.github.balamurali03.auth_sentinel_autoconfigure.model.CosmoPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.cert.X509Certificate;
import java.util.List;

public class CertificateAuthStrategy implements AuthStrategy {

    private static final String CERT_ATTRIBUTE = "jakarta.servlet.request.X509Certificate";

    @Override
    public boolean supports(HttpServletRequest request) {
        return request.getAttribute(CERT_ATTRIBUTE) != null;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {

        X509Certificate[] certs =
                (X509Certificate[]) request.getAttribute(CERT_ATTRIBUTE);

        if (certs == null || certs.length == 0) {
            throw new IllegalStateException("No X509 certificate found in request");
        }

        String subject = certs[0]
                .getSubjectX500Principal()
                .getName();   // RFC2253 format

        CosmoPrincipal principal =
                new CosmoPrincipal(
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
