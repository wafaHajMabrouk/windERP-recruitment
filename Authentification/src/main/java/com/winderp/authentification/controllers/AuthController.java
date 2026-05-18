package com.winderp.authentification.controllers;   // ← controllers (minuscule)

import com.winderp.authentification.models.User;     // ← models (minuscule)
import com.winderp.authentification.services.AuthService;
import com.winderp.authentification.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    // Constante pour éviter la duplication
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_INTERNAL_SERVER = "Erreur interne du serveur";
    private static final String ERROR_USER_NOT_FOUND = "Utilisateur non trouvé";
    private static final String ERROR_INVALID_PASSWORD = "Mot de passe incorrect";
    private static final String ERROR_ACCOUNT_NOT_APPROVED = "Compte non validé par admin";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            Map<String, Object> response = authService.register(user);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            log.warn("Erreur métier lors de l'inscription: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(MESSAGE_KEY, e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur interne lors de l'inscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MESSAGE_KEY, ERROR_INTERNAL_SERVER));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        try {
            String email = data.get("email");
            String password = data.get("password");
            Map<String, Object> response = authService.login(email, password);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            String message = e.getMessage();
            log.warn("Erreur d'authentification: {}", message);

            if (isAuthenticationError(message)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(MESSAGE_KEY, message));
            }
            return ResponseEntity.badRequest().body(Map.of(MESSAGE_KEY, message));
        } catch (Exception e) {
            log.error("Erreur interne lors de l'authentification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(MESSAGE_KEY, ERROR_INTERNAL_SERVER));
        }
    }

    private boolean isAuthenticationError(String message) {
        return ERROR_USER_NOT_FOUND.equals(message) ||
                ERROR_INVALID_PASSWORD.equals(message) ||
                ERROR_ACCOUNT_NOT_APPROVED.equals(message);
    }
}