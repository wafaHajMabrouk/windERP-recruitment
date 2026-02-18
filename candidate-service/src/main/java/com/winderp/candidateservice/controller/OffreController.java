package com.winderp.candidateservice.controller;


import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.SERVICE.OffreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OffreController {

    private final OffreService offreService;

    @PostMapping
    public Offre create(@RequestBody Offre offre) {
        return offreService.create(offre);
    }

    @GetMapping
    public List<Offre> getAll() {
        return offreService.getAll();
    }

    @GetMapping("/{id}")
    public Offre getById(@PathVariable Long id) {
        return offreService.getById(id);
    }

    @PutMapping("/{id}")
    public Offre update(@PathVariable Long id, @RequestBody Offre offre) {
        return offreService.update(id, offre);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        offreService.delete(id);
    }

    @GetMapping("/search/localisation")
    public List<Offre> searchByLocalisation(@RequestParam String localisation) {
        return offreService.searchByLocalisation(localisation);
    }

    @GetMapping("/search/keyword")
    public List<Offre> searchByKeyword(@RequestParam String keyword) {
        return offreService.searchByKeyword(keyword);
    }
}
