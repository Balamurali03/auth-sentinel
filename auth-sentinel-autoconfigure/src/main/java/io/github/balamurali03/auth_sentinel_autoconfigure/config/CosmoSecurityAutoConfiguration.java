package io.github.balamurali03.auth_sentinel_autoconfigure.config;

import io.github.balamurali03.auth_sentinel_autoconfigure.filter.CosmoSecurityFilter;
import io.github.balamurali03.auth_sentinel_autoconfigure.properties.CosmoJwtProperties;
import io.github.balamurali03.auth_sentinel_autoconfigure.resolver.AuthStrategyResolver;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.AuthStrategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.CertificateAuthStrategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.GatewayAuthStrategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.JwtAuthStrategy;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenService;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

/**
 * Auto-configuration that wires together the AuthSentinel security pipeline:
 *
 * <ol>
 *   <li>{@link CosmoTokenService}     – JWT generation and validation</li>
 *   <li>{@link AuthStrategy} beans    – JWT, Gateway, Certificate</li>
 *   <li>{@link AuthStrategyResolver}  – picks the right strategy per request</li>
 *   <li>{@link CosmoSecurityFilter}   – servlet filter that populates the SecurityContext</li>
 * </ol>
 *
 * <p>All beans are guarded with {@code @ConditionalOnMissingBean} so consumer
 * applications can override any component.
 */
@Configuration
@EnableConfigurationProperties(CosmoJwtProperties.class)
@ConditionalOnProperty(
    prefix = "cosmo.security",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class CosmoSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CosmoTokenService cosmoTokenService(CosmoJwtProperties props) {
        return new CosmoTokenServiceImpl(
                props.getSecret(),
                props.getExpiration(),
                props.getAlgorithm()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthStrategy jwtAuthStrategy(CosmoTokenService tokenService) {
        return new JwtAuthStrategy(tokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public GatewayAuthStrategy gatewayAuthStrategy() {
        return new GatewayAuthStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public CertificateAuthStrategy certificateAuthStrategy() {
        return new CertificateAuthStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthStrategyResolver authStrategyResolver(List<AuthStrategy> strategies) {
        return new AuthStrategyResolver(strategies);
    }

    @Bean
    @ConditionalOnMissingBean
    public CosmoSecurityFilter cosmoSecurityFilter(AuthStrategyResolver resolver) {
        return new CosmoSecurityFilter(resolver);
    }
}
