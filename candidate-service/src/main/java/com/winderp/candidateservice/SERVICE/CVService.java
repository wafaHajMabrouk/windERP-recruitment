package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.CV;
import com.winderp.candidateservice.Repository.CVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CVRepository cvRepository;

    public List<CV> getByCandidateId(Long candidateId) {
        return cvRepository.findByCandidate_Id(candidateId);
    }

    public List<CV> getByOffreId(Long offreId) {
        return cvRepository.findByOffre_Id(offreId);
    }

    public CV create(CV cv) {
        return cvRepository.save(cv);
    }

    public void delete(Long id) {
        cvRepository.deleteById(id);
    }
}
