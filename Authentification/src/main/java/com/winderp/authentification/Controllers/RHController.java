package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.RH;
import com.winderp.authentification.services.RHService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rh")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RHController {

    private final RHService rhService;

    @PostMapping
    public RH create(@RequestBody RH rh) {
        return rhService.createRH(rh);
    }

    @GetMapping
    public List<RH> getAll() {
        return rhService.getAllRH();
    }

    @GetMapping("/{id}")
    public RH getById(@PathVariable Long id) {
        return rhService.getRHById(id);
    }

    @PutMapping("/{id}")
    public RH update(@PathVariable Long id, @RequestBody RH rh) {
        return rhService.updateRH(id, rh);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!rhService.existsById(id)) {
            return ResponseEntity.status(404).body("RH avec id " + id + " non trouvé");
        }
        rhService.deleteRH(id);
        return ResponseEntity.ok("RH avec id " + id + " supprimé avec succès");
    }}
