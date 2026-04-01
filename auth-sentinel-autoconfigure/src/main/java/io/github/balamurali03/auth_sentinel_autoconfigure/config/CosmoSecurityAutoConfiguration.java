package io.github.balamurali03.auth_sentinel_autoconfigure.config;

import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenService;
import io.github.balamurali03.auth_sentinel_core.service.CosmoTokenServiceImpl;
import io.github.balamurali03.auth_sentinel_autoconfigure.filter.CosmoSecurityFilter;
import io.github.balamurali03.auth_sentinel_autoconfigure.properties.CosmoJwtProperties;
import io.github.balamurali03.auth_sentinel_autoconfigure.resolver.AuthStrategyResolver;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.AuthStrategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.CertificateAuthStrategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.GatewayAuthStrategy;
import io.github.balamurali03.auth_sentinel_autoconfigure.strategy.JwtAuthStrategy;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CosmoJwtProperties.class)
public class CosmoSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CosmoTokenService cosmoTokenService(CosmoJwtProperties properties) {

        return new CosmoTokenServiceImpl(
                properties.getSecret(),
                properties.getExpiration(),
                properties.getAlgorithm()
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
