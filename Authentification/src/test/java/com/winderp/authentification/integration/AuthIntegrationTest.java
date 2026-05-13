// AuthIntegrationTest.java - Version corrigée
package com.winderp.authentification.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Test complet d'inscription et connexion (CANDIDATE auto-approuvé)")
    void testRegisterAndLogin_Candidate() {
        // 1. Inscription CANDIDATE
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("email", "candidate@test.com");
        registerRequest.put("password", "candidate123");
        registerRequest.put("nom", "Candidate");
        registerRequest.put("prenom", "Test");
        registerRequest.put("role", "CANDIDATE");

        ResponseEntity<Map> registerResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/auth/register",
                registerRequest,
                Map.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResponse.getBody()).containsKey("id");
        assertThat(registerResponse.getBody().get("status")).isEqualTo("APPROVED");

        // 2. Connexion CANDIDATE
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "candidate@test.com");
        loginRequest.put("password", "candidate123");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/auth/login",
                loginRequest,
                Map.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).containsKey("token");
        assertThat(loginResponse.getBody().get("role")).isEqualTo("CANDIDATE");
    }

    @Test
    @DisplayName("Test login avec identifiants invalides")
    void testLogin_InvalidCredentials() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "wrong@test.com");
        loginRequest.put("password", "wrongpassword");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/auth/login",
                loginRequest,
                Map.class
        );

        // ✅ Correction: Vérifier le status code sans accéder au body
        assertThat(loginResponse.getStatusCode())
                .withFailMessage("Login avec identifiants invalides devrait retourner 401")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        // ✅ Optionnel: Vérifier que le body est null ou contient un message d'erreur
        if (loginResponse.getBody() != null) {
            assertThat(loginResponse.getBody().get("message"))
                    .isIn("Utilisateur non trouvé", "Mot de passe incorrect");
        }
    }

    @Test
    @DisplayName("Test login avec email correct mais mauvais mot de passe")
    void testLogin_WrongPassword() {
        // 1. D'abord créer un utilisateur valide
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("email", "valid@test.com");
        registerRequest.put("password", "correct123");
        registerRequest.put("nom", "Valid");
        registerRequest.put("prenom", "User");
        registerRequest.put("role", "CANDIDATE");

        restTemplate.postForEntity(getBaseUrl() + "/api/auth/register", registerRequest, Map.class);

        // 2. Tentative de connexion avec mauvais mot de passe
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "valid@test.com");
        loginRequest.put("password", "wrongpassword");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/auth/login",
                loginRequest,
                Map.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Test login avec email inexistant")
    void testLogin_NonExistentEmail() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "nonexistent@test.com");
        loginRequest.put("password", "anypassword");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/auth/login",
                loginRequest,
                Map.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}