package com.snippets.manager.snippetsmanager.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleIdTokenValidatorTests {

    private static final String CLIENT_ID = "my-client.apps.googleusercontent.com";

    private final OAuth2TokenValidator<Jwt> validator = SecurityConfig.googleIdTokenValidator(CLIENT_ID);

    @Test
    void acceptsTokensForThisClientWithEitherGoogleIssuer() {
        assertThat(validator.validate(token("https://accounts.google.com", CLIENT_ID, 3600)).hasErrors()).isFalse();
        assertThat(validator.validate(token("accounts.google.com", CLIENT_ID, 3600)).hasErrors()).isFalse();
    }

    @Test
    void rejectsTokensIssuedForAnotherApplication() {
        assertThat(validator.validate(token("https://accounts.google.com", "other-client", 3600)).hasErrors()).isTrue();
    }

    @Test
    void rejectsTokensFromAnotherIssuer() {
        assertThat(validator.validate(token("https://evil.example", CLIENT_ID, 3600)).hasErrors()).isTrue();
    }

    @Test
    void rejectsExpiredTokens() {
        assertThat(validator.validate(token("https://accounts.google.com", CLIENT_ID, -3600)).hasErrors()).isTrue();
    }

    private static Jwt token(String issuer, String audience, long expiresInSeconds) {
        Instant now = Instant.now();
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(issuer)
                .audience(List.of(audience))
                .subject("google-sub")
                .issuedAt(now.minusSeconds(Math.max(0, -expiresInSeconds) + 60))
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .build();
    }
}
