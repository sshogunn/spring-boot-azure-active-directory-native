package com.example.control.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAKey;
import java.util.Base64;

public class AzureAdJwtToken {
    private final CertificateLoader certificateLoader = new CertificateLoader();
    private final String token;
    private Header header;
    private Payload payload;

    AzureAdJwtToken(String token) {
        this.token = token;
        String[] parts = token.split("\\.");
        String header = parts[0];
        extractHeaderFields(header);
        String payload = parts[1];
        extractPayloadField(payload);
    }

    private void extractPayloadField(String part) {
        String payloadStr = new String(Base64.getUrlDecoder().decode((part)));
        JSONObject payload = new JSONObject(payloadStr);

        String issuer = payload.getString("iss");
        String ipAddr = payload.getString("ipaddr");
        String name = payload.getString("name");
        String uniqueName = payload.getString("unique_name");
        this.payload = new Payload(issuer, ipAddr, name, uniqueName);
    }

    private void extractHeaderFields(String part) {
        String headerStr = new String(Base64.getUrlDecoder().decode((part)));
        JSONObject header = new JSONObject(headerStr);
        String kid = header.getString("kid");
        this.header = new Header(kid);
    }
    
    public void verify() throws IOException, CertificateException {
        PublicKey publicKey = this.certificateLoader.loadPublicKey(header.kid);
        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAKey) publicKey)).withIssuer(payload.issuer).build();
        verifier.verify(token);
    }

    @Override
    public String toString() {
        return "AzureAdJwtToken " + payload;
    }

    @RequiredArgsConstructor
    private static final class Header {
        private final String kid;
    }

    @RequiredArgsConstructor
    @ToString
    private static final class Payload {
        private final String issuer;
        private final String ipAddr;
        private final String name;
        private final String uniqueName;
    }
}
