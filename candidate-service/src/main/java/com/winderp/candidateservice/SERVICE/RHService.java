package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.RH;
import com.winderp.candidateservice.Repository.RHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RHService {

    private final RHRepository rhRepository;

    public RH createRH(RH rh) {
        return rhRepository.save(rh);
    }

    public List<RH> getAllRH() {
        return rhRepository.findAll();
    }

    public RH getRHById(Long id) {
        return rhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RH not found"));
    }

    public RH updateRH(Long id, RH rhDetails) {
        RH rh = getRHById(id);
        rh.setNom(rhDetails.getNom());
        rh.setEmail(rhDetails.getEmail());
        rh.setPassword(rhDetails.getPassword());
        return rhRepository.save(rh);
    }

    public void deleteRH(Long id) {
        rhRepository.deleteById(id);
    }
}

