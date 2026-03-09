package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.Recruteur;
import com.winderp.authentification.services.RecruteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruteurs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RecruteurController {

    private final RecruteurService recruteurService;

    // CREATE
    @PostMapping
    public ResponseEntity<Recruteur> create(@RequestBody Recruteur recruteur) {

        Recruteur saved = recruteurService.create(recruteur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Recruteur>> getAll() {
        return ResponseEntity.ok(recruteurService.getAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Recruteur> getById(@PathVariable Long id) {
        return recruteurService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Recruteur> update(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        Recruteur updated = recruteurService.update(id, recruteur);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!recruteurService.existsById(id)) {
            return ResponseEntity.status(404).body("Recruteur avec id " + id + " non trouvé");
        }
        recruteurService.delete(id);
        return ResponseEntity.ok("Recruteur avec id " + id + " supprimé avec succès");
    }

    // SEARCH BY EMAIL
    @GetMapping("/search")
    public ResponseEntity<Recruteur> getByEmail(@RequestParam String email) {
        return recruteurService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CHECK EXISTS
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(recruteurService.existsById(id));
    }
}