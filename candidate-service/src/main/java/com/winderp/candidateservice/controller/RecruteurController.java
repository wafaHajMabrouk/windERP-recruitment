package com.winderp.candidateservice.controller;


import com.winderp.candidateservice.Models.Recruteur;
import com.winderp.candidateservice.SERVICE.RecruteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruteurs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RecruteurController {

    private final RecruteurService recruteurService;

    @PostMapping
    public Recruteur create(@RequestBody Recruteur recruteur) {
        return recruteurService.create(recruteur);
    }

    @GetMapping
    public List<Recruteur> getAll() {
        return recruteurService.getAll();
    }

    @GetMapping("/{id}")
    public Recruteur getById(@PathVariable Long id) {
        return recruteurService.getById(id);
    }

    @PutMapping("/{id}")
    public Recruteur update(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        return recruteurService.update(id, recruteur);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recruteurService.delete(id);
    }
}
