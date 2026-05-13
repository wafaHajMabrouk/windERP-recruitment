package com.winderp.authentification.services;

import com.winderp.authentification.Models.Admin;
import com.winderp.authentification.Models.Candidate;
import com.winderp.authentification.Models.RH;
import com.winderp.authentification.Models.Recruteur;
import com.winderp.authentification.Models.User;
import com.winderp.authentification.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Candidate testCandidate;

    @BeforeEach
    void setUp() {
        testCandidate = new Candidate();
        testCandidate.setId(1L);
        testCandidate.setEmail("candidate@test.com");
        testCandidate.setPassword("encodedPassword123");
        testCandidate.setNom("Dupont");
        testCandidate.setPrenom("Jean");
        testCandidate.setRole("CANDIDATE");
        testCandidate.setStatus("PENDING");
    }

    @Test
    @DisplayName("Test create user success")
    void testCreateUser_Success() {
        Candidate newCandidate = new Candidate();
        newCandidate.setEmail("new@test.com");
        newCandidate.setPassword("rawPassword");
        newCandidate.setNom("Nouveau");
        newCandidate.setPrenom("User");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(Candidate.class))).thenReturn(newCandidate);

        User result = userService.create(newCandidate);

        assertNotNull(result);
        assertEquals("new@test.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(newCandidate);
    }

    @Test
    @DisplayName("Test get by id success")
    void testGetById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCandidate));

        User result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("candidate@test.com", result.getEmail());
    }

    @Test
    @DisplayName("Test get by id not found")
    void testGetById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getById(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User not found with id: 99"));
    }

    @Test
    @DisplayName("Test find by email success")
    void testFindByEmail_Success() {
        when(userRepository.findByEmail("candidate@test.com"))
                .thenReturn(Optional.of(testCandidate));

        User result = userService.findByEmail("candidate@test.com");

        assertNotNull(result);
        assertEquals("candidate@test.com", result.getEmail());
    }

    @Test
    @DisplayName("Test find by email not found")
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.findByEmail("unknown@test.com");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User not found with email: unknown@test.com"));
    }

    @Test
    @DisplayName("Test approve user success")
    void testApproveUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(userRepository.save(any(Candidate.class))).thenReturn(testCandidate);

        User result = userService.approveUser(1L);

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(userRepository, times(1)).save(testCandidate);
    }

    @Test
    @DisplayName("Test reject user success")
    void testRejectUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(userRepository.save(any(Candidate.class))).thenReturn(testCandidate);

        User result = userService.rejectUser(1L);

        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(userRepository, times(1)).save(testCandidate);
    }

    @Test
    @DisplayName("Test change password success")
    void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(passwordEncoder.matches("oldPassword", "encodedPassword123")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.changePassword(1L, "oldPassword", "newPassword");

        assertEquals("newEncodedPassword", testCandidate.getPassword());
        verify(userRepository, times(1)).save(testCandidate);
    }

    @Test
    @DisplayName("Test change password failed - wrong old password")
    void testChangePassword_WrongOldPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCandidate));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.changePassword(1L, "wrongPassword", "newPassword");
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Ancien mot de passe incorrect", exception.getReason());
        verify(userRepository, never()).save(any());
    }
}