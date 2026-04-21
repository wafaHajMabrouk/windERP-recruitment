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

        if (userRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        String encodedPassword = passwordEncoder.encode(data.getPassword());
        User userToSave;

        String role = data.getRole().toUpperCase();

        switch (role) {
            case "ADMIN":
                Admin admin = new Admin();
                admin.setDepartement(data.getDepartement());
                copyCommon(data, admin, encodedPassword, role);
                admin.setStatus("APPROVED"); // ✅ ADMIN direct
                userToSave = admin;
                break;

            case "CANDIDATE":
                Candidate candidate = new Candidate();
                candidate.setAdresse(data.getAdresse());
                candidate.setCompetences(data.getCompetences());
                candidate.setNiveauExperience(data.getNiveauExperience());
                copyCommon(data, candidate, encodedPassword, role);
                candidate.setStatus("PENDING");
                userToSave = candidate;
                break;

            case "RH":
                RH rh = new RH();
                rh.setDepartement(data.getDepartement());
                rh.setNiveauResponsabilite(data.getNiveauResponsabilite());
                copyCommon(data, rh, encodedPassword, role);
                rh.setStatus("PENDING");
                userToSave = rh;
                break;

            case "RECRUTEUR":
                Recruteur recruteur = new Recruteur();
                recruteur.setEntreprise(data.getEntreprise());
                recruteur.setPoste(data.getPoste());
                recruteur.setSiteEntreprise(data.getSiteEntreprise());
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // ✅ LOGIQUE FINALE
        if (!"ADMIN".equals(user.getRole()) && !"APPROVED".equals(user.getStatus())) {
            throw new RuntimeException("Compte non validé par admin");
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("id", user.getId());
        response.put("email", user.getEmail());

        return response;
    }
}