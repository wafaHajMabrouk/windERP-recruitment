package com.winderp.candidateservice.service;

import com.winderp.candidateservice.models.Offre;
import com.winderp.candidateservice.models.Statut;
import com.winderp.candidateservice.repository.CandidatureRepository;
import com.winderp.candidateservice.repository.OffreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository repository;
    private final CandidatureRepository candidatureRepository;

    public Offre create(Offre offre) {
        offre.setStatut(Statut.OUVERT);
        return repository.save(offre);
    }

    public List<Offre> getAll() {
        List<Offre> offres = repository.findAll();
        offres.forEach(this::mettreAJourStatutSiNecessaire);
        return offres;
    }

    public Offre getById(Long id) {
        Offre offre = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable id=" + id));
        mettreAJourStatutSiNecessaire(offre);
        return offre;
    }

    public Offre update(Long id, Offre offreDetails) {
        Offre offre = getById(id);
        offre.setTitre(offreDetails.getTitre());
        offre.setDescription(offreDetails.getDescription());
        offre.setCategorie(offreDetails.getCategorie());
        offre.setMotCle(offreDetails.getMotCle());
        offre.setDateLimite(offreDetails.getDateLimite());
        offre.setMaxCandidatures(offreDetails.getMaxCandidatures());
        mettreAJourStatutSiNecessaire(offre);
        return repository.save(offre);
    }

    public void delete(Long id) {
        Offre offre = getById(id);
        repository.delete(offre);
    }

    public void rafraichirStatutOffre(Long offreId) {
        Offre offre = getById(offreId);
        mettreAJourStatutSiNecessaire(offre);
        repository.save(offre);
    }

    public List<Offre> getByCategorie(String categorie) {
        return repository.findByCategorie(categorie);
    }

    public List<Offre> getByMotCle(String motCle) {
        return repository.findByMotCleContainingIgnoreCase(motCle);
    }

    public List<Offre> getByStatut(Statut statut) {
        return repository.findByStatut(statut);
    }

    public List<Offre> getByCategorieAndMotCle(String categorie, String motCle) {
        return repository.findByCategorieAndMotCleContainingIgnoreCase(categorie, motCle);
    }

    public List<Offre> getOffresOuvertes() {
        return repository.findOffresOuvertes(Statut.OUVERT);
    }

    private void mettreAJourStatutSiNecessaire(Offre offre) {
        if (offre.getStatut() == Statut.FERME) return;

        LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        boolean estDepassee = offre.getDateLimite() != null &&
                offre.getDateLimite().isBefore(today);

        long nbCandidatures = candidatureRepository.countByOffreId(offre.getId());
        boolean maxAtteint = offre.getMaxCandidatures() != null &&
                nbCandidatures >= offre.getMaxCandidatures();

        if (estDepassee || maxAtteint) {
            offre.setStatut(Statut.FERME);
        } else {
            offre.setStatut(Statut.OUVERT);
        }
    }

    public List<Offre> searchOffres(String motCle, String categorie, Statut statut, Boolean ouvertesSeulement) {
        List<Offre> offres;

        if (motCle != null && !motCle.trim().isEmpty() && categorie != null && !categorie.trim().isEmpty()) {
            offres = repository.findByCategorieAndMotCleContainingIgnoreCase(categorie.trim(), motCle.trim());
        } else if (motCle != null && !motCle.trim().isEmpty()) {
            offres = repository.findByMotCleContainingIgnoreCase(motCle.trim());
        } else if (categorie != null && !categorie.trim().isEmpty()) {
            offres = repository.findByCategorie(categorie.trim());
        } else {
            offres = repository.findAll();
        }

        if (statut != null) {
            offres = offres.stream()
                    .filter(o -> o.getStatut() == statut)
                    .toList();
        }

        if (Boolean.TRUE.equals(ouvertesSeulement)) {
            offres = offres.stream()
                    .filter(o -> o.getStatut() == Statut.OUVERT)
                    .toList();
        }

        offres.forEach(this::mettreAJourStatutSiNecessaire);
        return offres;
    }

    public long countOffresOuvertes() {
        return repository.countByStatut(Statut.OUVERT);
    }

    public long countOffresFermees() {
        return repository.countByStatut(Statut.FERME);
    }
}