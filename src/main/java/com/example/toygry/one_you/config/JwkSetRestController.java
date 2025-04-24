package com.example.toygry.one_you.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * Controller that exposes the JWKS (JSON Web Key Set) endpoint
 * This endpoint is used by clients to validate JWT tokens
 */
@RestController
public class JwkSetRestController {

    private final JWKSet jwkSet;

    @Autowired
    public JwkSetRestController(RSAPublicKey publicKey, RSAPrivateKey privateKey, String keyId) {
        // Create RSAKey from public and private keys
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();

        this.jwkSet = new JWKSet(rsaKey);
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return this.jwkSet.toJSONObject();
    }
}
