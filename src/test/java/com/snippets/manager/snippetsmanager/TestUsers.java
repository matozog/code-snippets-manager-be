package com.snippets.manager.snippetsmanager;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/**
 * Authenticates MockMvc requests as a signed-in Google user, without a real Google ID token.
 */
public final class TestUsers {

    private TestUsers() {
    }

    public static RequestPostProcessor alice() {
        return user("google-sub-alice", "alice@example.com", "Alice");
    }

    public static RequestPostProcessor bob() {
        return user("google-sub-bob", "bob@example.com", "Bob");
    }

    public static RequestPostProcessor user(String googleSub, String email, String name) {
        return jwt().jwt(token -> token.subject(googleSub).claim("email", email).claim("name", name));
    }
}
