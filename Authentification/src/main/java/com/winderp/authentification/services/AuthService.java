package com.winderp.authentification.services;

import com.winderp.authentification.Models.*;
import com.winderp.authentification.Repository.UserRepository;
import com.winderp.authentification.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> register(User data) {
        // Vérification des données
        if (data == null || data.getEmail() == null || data.getPassword() == null || data.getRole() == null) {
            throw new RuntimeException("Données d'inscription incomplètes");
        }

        if (userRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        String encodedPassword = passwordEncoder.encode(data.getPassword());
        User userToSave;
        String role = data.getRole().toUpperCase();

        switch (role) {
            case "ADMIN":
                Admin admin = new Admin();
                if (data.getDepartement() != null) {
                    admin.setDepartement(data.getDepartement());
                }
                copyCommon(data, admin, encodedPassword, role);
                admin.setStatus("APPROVED");
                userToSave = admin;
                break;

            case "CANDIDATE":
                Candidate candidate = new Candidate();
                if (data.getAdresse() != null) candidate.setAdresse(data.getAdresse());
                if (data.getCompetences() != null) candidate.setCompetences(data.getCompetences());
                if (data.getNiveauExperience() != null) candidate.setNiveauExperience(data.getNiveauExperience());
                copyCommon(data, candidate, encodedPassword, role);
                candidate.setStatus("PENDING");
                userToSave = candidate;
                break;

            case "RH":
                RH rh = new RH();
                if (data.getDepartement() != null) rh.setDepartement(data.getDepartement());
                if (data.getNiveauResponsabilite() != null) rh.setNiveauResponsabilite(data.getNiveauResponsabilite());
                copyCommon(data, rh, encodedPassword, role);
                rh.setStatus("PENDING");
                userToSave = rh;
                break;

            case "RECRUTEUR":
                Recruteur recruteur = new Recruteur();
                if (data.getEntreprise() != null) recruteur.setEntreprise(data.getEntreprise());
                if (data.getPoste() != null) recruteur.setPoste(data.getPoste());
                if (data.getSiteEntreprise() != null) recruteur.setSiteEntreprise(data.getSiteEntreprise());
                copyCommon(data, recruteur, encodedPassword, role);
                recruteur.setStatus("PENDING");
                userToSave = recruteur;
                break;

            default:
                throw new RuntimeException("Rôle invalide");
        }

        User savedUser = userRepository.save(userToSave);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole());
        response.put("status", savedUser.getStatus());

        return response;
    }

    private void copyCommon(User source, User target, String password, String role) {
        target.setEmail(source.getEmail());
        target.setNom(source.getNom());
        target.setPrenom(source.getPrenom());
        target.setPassword(password);
        target.setRole(role);
    }

    public Map<String, Object> login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email requis");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Mot de passe requis");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // ADMIN peut toujours se connecter, les autres doivent être APPROVED
        if (!"ADMIN".equals(user.getRole()) && !"APPROVED".equals(user.getStatus())) {
            throw new RuntimeException("Compte non validé par admin");
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("status", user.getStatus());

        return response;
    }
}