package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.User;
import com.winderp.authentification.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            Map<String, Object> response = authService.register(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        try {
            String email = data.get("email");
            String password = data.get("password");
            Map<String, Object> response = authService.login(email, password);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String message = e.getMessage();

            // Retourner 401 pour les erreurs d'authentification, 400 pour les autres
            if (message.equals("Utilisateur non trouvé") ||
                    message.equals("Mot de passe incorrect") ||
                    message.equals("Compte non validé par admin")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", message));
            }
            return ResponseEntity.badRequest().body(Map.of("message", message));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erreur interne du serveur"));
        }
    }
}