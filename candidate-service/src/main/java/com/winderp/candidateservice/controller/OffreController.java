package com.winderp.candidateservice.controller;

import com.winderp.candidateservice.models.Offre;
import com.winderp.candidateservice.models.Statut;
import com.winderp.candidateservice.service.OffreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OffreController {

    private final OffreService offreService;

    // ==================== CRUD ====================
    @PostMapping
    public Offre create(@RequestBody Offre offre) {
        return offreService.create(offre);
    }

    @GetMapping
    public List<Offre> getAll() {
        return offreService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offre> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(offreService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offre> update(@PathVariable Long id, @RequestBody Offre offre) {
        try {
            return ResponseEntity.ok(offreService.update(id, offre));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            offreService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== FILTRES AVANCÉS ====================

    @GetMapping("/search")
    public ResponseEntity<List<Offre>> searchOffres(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) Statut statut,
            @RequestParam(required = false) Boolean ouvertesSeulement) {   // true = seulement les offres ouvertes

        List<Offre> result = offreService.searchOffres(motCle, categorie, statut, ouvertesSeulement);
        return ResponseEntity.ok(result);
    }

    // Filtres simples (gardés pour compatibilité)
    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<Offre>> getByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(offreService.getByCategorie(categorie));
    }

    @GetMapping("/motcle/{motCle}")
    public ResponseEntity<List<Offre>> getByMotCle(@PathVariable String motCle) {
        return ResponseEntity.ok(offreService.getByMotCle(motCle));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Offre>> getByStatut(@PathVariable Statut statut) {
        return ResponseEntity.ok(offreService.getByStatut(statut));
    }

    @GetMapping("/ouvertes")
    public ResponseEntity<List<Offre>> getOffresOuvertes() {
        return ResponseEntity.ok(offreService.getOffresOuvertes());
    }
    @GetMapping("/count/ouvertes")
    public long countOuvertes() {
        return offreService.countOffresOuvertes();
    }

    @GetMapping("/count/fermees")
    public long countFermees() {
        return offreService.countOffresFermees();
    }
}