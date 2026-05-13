package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.*;
import com.winderp.authentification.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new HashMap<>();
    }

    @Test
    @DisplayName("Test login success")
    void testLogin_Success() throws Exception {
        loginRequest.put("email", "candidate@test.com");
        loginRequest.put("password", "password123");

        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("token", "jwt-token-123");
        authResponse.put("role", "CANDIDATE");
        authResponse.put("id", 1L);
        authResponse.put("email", "candidate@test.com");

        when(authService.login(anyString(), anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.role").value("CANDIDATE"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("candidate@test.com"));

        verify(authService, times(1)).login("candidate@test.com", "password123");
    }

    @Test
    @DisplayName("Test login failed - Invalid credentials")
    void testLogin_InvalidCredentials() throws Exception {
        loginRequest.put("email", "wrong@test.com");
        loginRequest.put("password", "wrongpassword");

        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Utilisateur non trouvé"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test login failed - Account not approved")
    void testLogin_AccountNotApproved() throws Exception {
        loginRequest.put("email", "pending@test.com");
        loginRequest.put("password", "password123");

        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Compte non validé par admin"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test register - Candidate success")
    void testRegister_Candidate_Success() throws Exception {
        Map<String, Object> candidateData = new HashMap<>();
        candidateData.put("email", "candidate@test.com");
        candidateData.put("password", "password123");
        candidateData.put("nom", "Dupont");
        candidateData.put("prenom", "Jean");
        candidateData.put("role", "CANDIDATE");
        candidateData.put("adresse", "Paris");
        candidateData.put("competences", "Java, Spring");
        candidateData.put("niveauExperience", "3 ans");

        Map<String, Object> registerResponse = new HashMap<>();
        registerResponse.put("id", 1L);
        registerResponse.put("email", "candidate@test.com");
        registerResponse.put("role", "CANDIDATE");
        registerResponse.put("status", "PENDING");

        when(authService.register(any(User.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("candidate@test.com"))
                .andExpect(jsonPath("$.role").value("CANDIDATE"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Test register - Admin success")
    void testRegister_Admin_Success() throws Exception {
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("email", "admin@test.com");
        adminData.put("password", "admin123");
        adminData.put("nom", "Admin");
        adminData.put("prenom", "System");
        adminData.put("role", "ADMIN");
        adminData.put("departement", "IT");

        Map<String, Object> registerResponse = new HashMap<>();
        registerResponse.put("id", 2L);
        registerResponse.put("email", "admin@test.com");
        registerResponse.put("role", "ADMIN");
        registerResponse.put("status", "APPROVED");

        when(authService.register(any(User.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}