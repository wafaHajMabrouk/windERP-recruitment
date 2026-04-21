package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.User;
import com.winderp.authentification.Repository.UserRepository;
import com.winderp.authentification.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")

public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.create(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    // Batch endpoint: returns users for the provided IDs (missing IDs are ignored)
    @PostMapping("/batch")
    public ResponseEntity<List<User>> getUsersByIds(@RequestBody List<Long> ids) {
        List<User> users = userService.findAllById(ids);
        return ResponseEntity.ok(users);
    }
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id,
                                            @RequestBody Map<String, String> payload) {
        try {
            String oldPassword = payload.get("oldPassword");
            String newPassword = payload.get("newPassword");
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Les champs oldPassword et newPassword sont requis.");
            }
            userService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); // ou log.error
            throw e;
        }
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.rejectUser(id));
    }
    @GetMapping("/{id}/name")
    public ResponseEntity<String> getUserName(@PathVariable Long id) {
        // ✅ Appel non statique sur l'instance injectée
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user.getNom()))  // adaptez getFullName() si nécessaire
                .orElse(ResponseEntity.notFound().build());
    }
}
