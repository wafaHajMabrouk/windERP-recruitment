package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.Offre;
import com.winderp.candidateservice.Models.Statut;
import com.winderp.candidateservice.Repository.CandidatureRepository;
import com.winderp.candidateservice.Repository.OffreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService {

    private final OffreRepository repository;
    private final CandidatureRepository candidatureRepository;

    // Création : statut calculé
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
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));
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

    // Méthode publique pour rafraîchir le statut (appelée par CandidatureService)
    public void rafraichirStatutOffre(Long offreId) {
        Offre offre = getById(offreId);
        mettreAJourStatutSiNecessaire(offre);
        repository.save(offre);
    }

    // ==================== MÉTHODES DE FILTRAGE ====================

    // Récupérer les offres par catégorie
    public List<Offre> getByCategorie(String categorie) {
        return repository.findByCategorie(categorie);
    }

    // Récupérer les offres par mot clé (recherche insensible à la casse)
    public List<Offre> getByMotCle(String motCle) {
        return repository.findByMotCleContainingIgnoreCase(motCle);
    }

    // Récupérer les offres par statut
    public List<Offre> getByStatut(Statut statut) {
        return repository.findByStatut(statut);
    }

    // Recherche avancée : catégorie ET mot clé
    public List<Offre> getByCategorieAndMotCle(String categorie, String motCle) {
        return repository.findByCategorieAndMotCleContainingIgnoreCase(categorie, motCle);
    }

    // Récupérer les offres encore ouvertes (non expirées et non fermées)
    public List<Offre> getOffresOuvertes() {
        return repository.findOffresOuvertes(Statut.OUVERT);
    }

    // ==================== MÉTHODE PRIVÉE ====================

    // Calcule et met à jour le statut en fonction de la date et du nombre de candidatures
    private void mettreAJourStatutSiNecessaire(Offre offre) {
        if (offre.getStatut() == Statut.FERME) return;

        boolean estDepassee = offre.getDateLimite() != null &&
                offre.getDateLimite().isBefore(LocalDate.now());

        long nbCandidatures = candidatureRepository.countByOffreId(offre.getId());
        boolean maxAtteint = offre.getMaxCandidatures() != null &&
                nbCandidatures >= offre.getMaxCandidatures();

        if (estDepassee || maxAtteint) {
            offre.setStatut(Statut.FERME);
        } else {
            offre.setStatut(Statut.OUVERT);
        }
    }
    // ==================== AJOUTER CETTE MÉTHODE DANS OffreService.java ====================

    public List<Offre> searchOffres(String motCle, String categorie, Statut statut, Boolean ouvertesSeulement) {
        List<Offre> offres;

        // Recherche combinée selon les paramètres fournis
        if (motCle != null && !motCle.trim().isEmpty() && categorie != null && !categorie.trim().isEmpty()) {
            offres = repository.findByCategorieAndMotCleContainingIgnoreCase(categorie.trim(), motCle.trim());
        }
        else if (motCle != null && !motCle.trim().isEmpty()) {
            offres = repository.findByMotCleContainingIgnoreCase(motCle.trim());
        }
        else if (categorie != null && !categorie.trim().isEmpty()) {
            offres = repository.findByCategorie(categorie.trim());
        }
        else {
            offres = repository.findAll();
        }

        // Filtre par statut si fourni
        if (statut != null) {
            offres = offres.stream()
                    .filter(o -> o.getStatut() == statut)
                    .toList();
        }

        // Filtre "ouvertes seulement"
        if (Boolean.TRUE.equals(ouvertesSeulement)) {
            offres = offres.stream()
                    .filter(o -> o.getStatut() == Statut.OUVERT)
                    .toList();
        }

        // Mise à jour du statut avant retour (important)
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