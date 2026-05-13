package com.winderp.authentification.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AppConfig.class})
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoder() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testPasswordEncoderWithDifferentPasswords() {
        String password1 = "password123";
        String password2 = "differentPassword";

        String encoded1 = passwordEncoder.encode(password1);
        String encoded2 = passwordEncoder.encode(password2);

        assertTrue(passwordEncoder.matches(password1, encoded1));
        assertTrue(passwordEncoder.matches(password2, encoded2));
        assertFalse(passwordEncoder.matches(password1, encoded2));
    }
}