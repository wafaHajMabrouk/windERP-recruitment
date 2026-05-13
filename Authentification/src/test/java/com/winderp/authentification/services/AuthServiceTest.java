package com.winderp.authentification.services;

import com.winderp.authentification.Models.Admin;
import com.winderp.authentification.Models.Candidate;
import com.winderp.authentification.Models.User;
import com.winderp.authentification.Repository.UserRepository;
import com.winderp.authentification.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Candidate testCandidate;
    private Admin testAdmin;

    @BeforeEach
    void setUp() {

        testCandidate = new Candidate();
        testCandidate.setId(1L);
        testCandidate.setEmail("candidate@test.com");
        testCandidate.setPassword("encodedPassword123");
        testCandidate.setNom("Dupont");
        testCandidate.setPrenom("Jean");
        testCandidate.setRole("CANDIDATE");
        testCandidate.setStatus("APPROVED");
        testCandidate.setCompetences("Java, Spring");
        testCandidate.setNiveauExperience("3 ans");

        testAdmin = new Admin();
        testAdmin.setId(2L);
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword("encodedAdmin123");
        testAdmin.setNom("Admin");
        testAdmin.setPrenom("System");
        testAdmin.setRole("ADMIN");
        testAdmin.setStatus("APPROVED");
        testAdmin.setDepartement("IT");
    }

    @Test
    @DisplayName("Test login success - Candidate")
    void testLogin_Success_Candidate() {

        when(userRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.of(testCandidate));

        when(passwordEncoder.matches("password123", "encodedPassword123"))
                .thenReturn(true);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {

            jwtUtilMock.when(() -> JwtUtil.generateToken(anyString(), anyString()))
                    .thenReturn("jwt-token-123");

            Map<String, Object> result =
                    authService.login("candidate@test.com", "password123");

            assertNotNull(result);
            assertEquals("CANDIDATE", result.get("role"));
            assertEquals(1L, result.get("id"));
            assertEquals("candidate@test.com", result.get("email"));
            assertNotNull(result.get("token"));
        }

        verify(userRepository, times(1))
                .findByEmail("candidate@test.com");
    }

    @Test
    @DisplayName("Test login success - Admin")
    void testLogin_Success_Admin() {

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(testAdmin));

        when(passwordEncoder.matches("admin123", "encodedAdmin123"))
                .thenReturn(true);

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {

            jwtUtilMock.when(() -> JwtUtil.generateToken(anyString(), anyString()))
                    .thenReturn("jwt-token-123");

            Map<String, Object> result =
                    authService.login("admin@test.com", "admin123");

            assertNotNull(result);
            assertEquals("ADMIN", result.get("role"));
            assertEquals(2L, result.get("id"));
        }
    }

    @Test
    @DisplayName("Test login failed - User not found")
    void testLogin_UserNotFound() {

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    authService.login("unknown@test.com", "password123");
                });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    @DisplayName("Test login failed - Wrong password")
    void testLogin_WrongPassword() {

        when(userRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.of(testCandidate));

        when(passwordEncoder.matches("wrongpassword", "encodedPassword123"))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    authService.login("candidate@test.com", "wrongpassword");
                });

        assertEquals("Mot de passe incorrect", exception.getMessage());
    }

    @Test
    @DisplayName("Test login failed - Account not approved")
    void testLogin_AccountNotApproved() {

        testCandidate.setStatus("PENDING");

        when(userRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.of(testCandidate));

        when(passwordEncoder.matches("password123", "encodedPassword123"))
                .thenReturn(true);

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    authService.login("candidate@test.com", "password123");
                });

        assertEquals("Compte non validé par admin", exception.getMessage());
    }

    @Test
    @DisplayName("Test register - Candidate success")
    void testRegister_Candidate_Success() {

        Candidate newCandidate = new Candidate();

        newCandidate.setEmail("new@test.com");
        newCandidate.setPassword("rawPassword123");
        newCandidate.setNom("Nouveau");
        newCandidate.setPrenom("User");
        newCandidate.setRole("CANDIDATE");
        newCandidate.setAdresse("Paris");
        newCandidate.setCompetences("Java");
        newCandidate.setNiveauExperience("Junior");

        when(userRepository.findByEmail("new@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("rawPassword123"))
                .thenReturn("encodedPassword123");

        when(userRepository.save(any(Candidate.class)))
                .thenReturn(newCandidate);

        Map<String, Object> result =
                authService.register(newCandidate);

        assertNotNull(result);
        assertEquals("new@test.com", result.get("email"));
        assertEquals("CANDIDATE", result.get("role"));
        assertEquals("PENDING", result.get("status"));
    }

    @Test
    @DisplayName("Test register - Admin success")
    void testRegister_Admin_Success() {

        Admin newAdmin = new Admin();

        newAdmin.setEmail("newadmin@test.com");
        newAdmin.setPassword("admin123");
        newAdmin.setNom("New");
        newAdmin.setPrenom("Admin");
        newAdmin.setRole("ADMIN");
        newAdmin.setDepartement("Security");

        when(userRepository.findByEmail("newadmin@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("admin123"))
                .thenReturn("encodedAdmin123");

        Admin savedAdmin = new Admin();

        savedAdmin.setId(2L);
        savedAdmin.setEmail("newadmin@test.com");
        savedAdmin.setNom("New");
        savedAdmin.setPrenom("Admin");
        savedAdmin.setRole("ADMIN");
        savedAdmin.setStatus("APPROVED");
        savedAdmin.setDepartement("Security");
        savedAdmin.setPassword("encodedAdmin123");

        when(userRepository.save(any(Admin.class)))
                .thenReturn(savedAdmin);

        Map<String, Object> result =
                authService.register(newAdmin);

        assertNotNull(result);
        assertEquals("newadmin@test.com", result.get("email"));
        assertEquals("ADMIN", result.get("role"));
        assertEquals("APPROVED", result.get("status"));
    }

    @Test
    @DisplayName("Test register failed - Email already used")
    void testRegister_EmailAlreadyUsed() {

        Candidate existingCandidate = new Candidate();

        existingCandidate.setNom("Dupont");
        existingCandidate.setPrenom("Jean");
        existingCandidate.setEmail("existing@test.com");
        existingCandidate.setPassword("password123");
        existingCandidate.setRole("CANDIDATE");
        existingCandidate.setAdresse("Paris");
        existingCandidate.setCompetences("Java");
        existingCandidate.setNiveauExperience("Junior");

        when(userRepository.findByEmail("existing@test.com"))
                .thenReturn(Optional.of(existingCandidate));

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    authService.register(existingCandidate);
                });

        assertEquals("Email déjà utilisé", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test register failed - Invalid role")
    void testRegister_InvalidRole() {

        User invalidUser = new User() {
        };

        invalidUser.setNom("Invalid");
        invalidUser.setPrenom("User");
        invalidUser.setEmail("invalid@test.com");
        invalidUser.setPassword("password123");
        invalidUser.setRole("INVALID_ROLE");

        when(userRepository.findByEmail("invalid@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> {
                    authService.register(invalidUser);
                });

        assertEquals(
                "Rôle invalide: INVALID_ROLE",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any());
    }
}