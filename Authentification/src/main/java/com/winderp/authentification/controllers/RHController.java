package com.winderp.authentification.controllers;

import com.winderp.authentification.models.RH;
import com.winderp.authentification.services.RHService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rh")
@RequiredArgsConstructor
@CrossOrigin("*")

public class RHController {

    private final RHService rhService;

    // CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RH rh) {
        try {
            RH saved = rhService.create(rh);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<RH>> getAll() {
        return ResponseEntity.ok(rhService.getAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            RH rh = rhService.getById(id);
            return ResponseEntity.ok(rh);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RH rh) {
        try {
            RH updated = rhService.update(id, rh);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!rhService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("RH avec id " + id + " non trouvé");
        }
        rhService.delete(id);
        return ResponseEntity.ok("RH avec id " + id + " supprimé avec succès");
    }

    // EXISTS
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(rhService.existsById(id));
    }
}