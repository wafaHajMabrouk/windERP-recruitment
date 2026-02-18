package com.winderp.candidateservice.controller;


import com.winderp.candidateservice.Models.RH;
import com.winderp.candidateservice.SERVICE.RHService;
import lombok.RequiredArgsConstructor;
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
    public void delete(@PathVariable Long id) {
        rhService.deleteRH(id);
    }
}
