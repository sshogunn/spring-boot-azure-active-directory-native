package com.example.control.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultRequestEnhancer;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.MultiValueMap;

@EnableOAuth2Sso // enabling login from OAuth (AzureAD)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // enabling the authorization check before each service call.
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AzureAdJwtAuthenticationTokenFilter azureAdJwtAuthenticationTokenFilter;

    @Bean
    public UserInfoRestTemplateCustomizer getUserInfoRestTemplateCustomizer() {
        return template -> template.setAccessTokenProvider(new MyAuthorizationCodeAccessTokenProvider());
    }

    protected class MyAuthorizationCodeAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {
        MyAuthorizationCodeAccessTokenProvider() {
            setTokenRequestEnhancer(new DefaultRequestEnhancer() {
                @Override
                public void enhance(AccessTokenRequest request, OAuth2ProtectedResourceDetails resource, MultiValueMap<String, String> form, HttpHeaders headers) {
                    super.enhance(request, resource, form, headers);
                    form.set("resource", "https://graph.windows.net/");
                }
            });
        }
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureAccess(http);
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        addAzureRoleAssignmentFilter(http);
        http.headers().frameOptions().sameOrigin();
    }

    private void configureAccess(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/home").permitAll();
        http.authorizeRequests().antMatchers("/api/**").authenticated();
        http.logout().logoutSuccessUrl("/").permitAll();
        http.authorizeRequests().anyRequest().permitAll();
    }

    private void addAzureRoleAssignmentFilter(HttpSecurity http) {
        // need a filter to validate the Jwt token from AzureAD and assign roles.
        // without this, the token will not be validated and the role is always ROLE_USER.
        http.addFilterBefore(azureAdJwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
