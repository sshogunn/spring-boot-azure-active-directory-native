package com.example.control.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AzureAdJwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AzureAdJwtAuthenticationTokenFilter.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader(TOKEN_HEADER);
        if (tokenHeader != null) {
            if (tokenHeader.startsWith(TOKEN_TYPE)) {
                String jwtText = tokenHeader.substring(TOKEN_TYPE.length());
                LOG.debug("Raw JWT token: >>{}<<", jwtText);
                AzureAdJwtToken jwt = new AzureAdJwtToken(jwtText);
                LOG.debug("JWT: {}", jwt);
                verifyToken(jwt);
                Authentication authentication = toAuthentication(jwt);
                LOG.info("Request token verification success. {}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            LOG.warn("This request does not contain any authorization token.");
        }

        filterChain.doFilter(request, response);
    }

    private static void verifyToken(AzureAdJwtToken jwt) {
        try {
            jwt.verify();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Token verification success!");
            }
        } catch (IOException|CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    private static Authentication toAuthentication(AzureAdJwtToken jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_group1"));
        Authentication authentication = new PreAuthenticatedAuthenticationToken(jwt, null, authorities);
        authentication.setAuthenticated(true);
        return authentication;
    }
}