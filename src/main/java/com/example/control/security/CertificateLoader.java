package com.example.control.security;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateLoader {

    PublicKey loadPublicKey(String sourceKid) throws IOException, CertificateException {
        String openidConfigStr = readUrl("https://login.microsoftonline.com/common/.well-known/openid-configuration");
        JSONObject openidConfig = new JSONObject(openidConfigStr);

        String jwksUri = openidConfig.getString("jwks_uri");

        String jwkConfigStr = readUrl(jwksUri);
        JSONObject jwkConfig = new JSONObject(jwkConfigStr);

        JSONArray keys = jwkConfig.getJSONArray("keys");
        for (int i = 0; i < keys.length(); i++) {
            JSONObject key = keys.getJSONObject(i);

            String kid = key.getString("kid");
            String x5c = key.getJSONArray("x5c").getString(0);
            String keyStr = buildCertificate(x5c);

            // read certification
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            InputStream stream = new ByteArrayInputStream(keyStr.getBytes(StandardCharsets.US_ASCII));
            X509Certificate cer = (X509Certificate) fact.generateCertificate(stream);
            PublicKey publicKey = cer.getPublicKey();
            if (sourceKid.equals(kid)) {
                return publicKey;
            }
        }
        return null;
    }

    private String buildCertificate(String x5c) {
        StringBuilder result = new StringBuilder("-----BEGIN CERTIFICATE-----\r\n");
        String tmp = x5c;
        while (tmp.length() > 0) {
            if (tmp.length() > 64) {
                String x = tmp.substring(0, 64);
                result.append(x);
                result.append("\r\n");
                tmp = tmp.substring(64);
            } else {
                result.append(tmp);
                result.append("\r\n");
                tmp = "";
            }
        }
        result.append("-----END CERTIFICATE-----\r\n");
        return result.toString();
    }

    private String readUrl(String url) throws IOException {
        URL address = new URL(url);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(address.openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        return sb.toString();
    }
}
