package io.github.balamurali03.auth_sentinel_autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Web-layer security toggles for AuthSentinel.
 *
 * <pre>
 * cosmo:
 *   security:
 *     web:
 *       enable-form-login: false
 *       enable-http-basic: false
 *       enable-csrf: false
 *       enable-logout: false
 * </pre>
 */
@ConfigurationProperties(prefix = "cosmo.security.web")
public class CosmoSecurityWebProperties {

    private boolean enableFormLogin = false;
    private boolean enableHttpBasic = false;
    private boolean enableCsrf      = false;
    private boolean enableLogout    = false;

    public boolean isEnableFormLogin()        { return enableFormLogin; }
    public void setEnableFormLogin(boolean v) { this.enableFormLogin = v; }

    public boolean isEnableHttpBasic()        { return enableHttpBasic; }
    public void setEnableHttpBasic(boolean v) { this.enableHttpBasic = v; }

    public boolean isEnableCsrf()             { return enableCsrf; }
    public void setEnableCsrf(boolean v)      { this.enableCsrf = v; }

    public boolean isEnableLogout()           { return enableLogout; }
    public void setEnableLogout(boolean v)    { this.enableLogout = v; }
}
