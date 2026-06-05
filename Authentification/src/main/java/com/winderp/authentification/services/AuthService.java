package com.winderp.authentification.services;

import com.winderp.authentification.models.Candidate;
import com.winderp.authentification.models.User;
import com.winderp.authentification.repository.UserRepository;
import com.winderp.authentification.config.JwtUtil;
import com.winderp.authentification.exception.BusinessException;
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

    private static final String ROLE_CANDIDATE = "CANDIDATE";
    private static final String STATUS_APPROVED = "APPROVED"; // plus besoin d'attente admin

    private static final String ERROR_EMAIL_REQUIRED = "Email requis";
    private static final String ERROR_CREDENTIAL_REQUIRED = "Mot de passe requis";
    private static final String ERROR_INCOMPLETE_DATA = "Données d'inscription incomplètes";
    private static final String ERROR_EMAIL_ALREADY_USED = "Email déjà utilisé";
    private static final String ERROR_USER_NOT_FOUND = "Utilisateur non trouvé";
    private static final String ERROR_INVALID_CREDENTIAL = "Mot de passe incorrect";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> register(User data) {
        validateRegistrationData(data);
        checkEmailNotExists(data.getEmail());

        String encodedPassword = passwordEncoder.encode(data.getPassword());
        // Seul le rôle CANDIDATE est accepté
        Candidate candidate = createCandidate(data, encodedPassword);
        User savedUser = userRepository.save(candidate);
        return buildUserResponse(savedUser);
    }

    private void validateRegistrationData(User data) {
        if (data == null || data.getEmail() == null || data.getPassword() == null) {
            throw new BusinessException(ERROR_INCOMPLETE_DATA);
        }
        // Vérification des champs obligatoires pour un candidat
        if (data.getNom() == null || data.getPrenom() == null ||
                data.getAdresse() == null || data.getCompetences() == null ||
                data.getNiveauExperience() == null) {
            throw new BusinessException("Tous les champs candidat sont obligatoires (nom, prénom, adresse, compétences, niveau d'expérience)");
        }
    }

    private void checkEmailNotExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ERROR_EMAIL_ALREADY_USED);
        }
    }

    private Candidate createCandidate(User data, String encodedPassword) {
        Candidate candidate = new Candidate();
        candidate.setEmail(data.getEmail());
        candidate.setNom(data.getNom());
        candidate.setPrenom(data.getPrenom());
        candidate.setPassword(encodedPassword);
        candidate.setRole(ROLE_CANDIDATE);
        candidate.setStatus(STATUS_APPROVED); // validation immédiate
        candidate.setAdresse(data.getAdresse());
        candidate.setCompetences(data.getCompetences());
        candidate.setNiveauExperience(data.getNiveauExperience());
        return candidate;
    }

    public Map<String, Object> login(String email, String password) {
        validateLoginData(email, password);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ERROR_USER_NOT_FOUND));

        validatePassword(password, user);

        // Plus de vérification de statut : tous les comptes sont directement APPROVED
        String token = JwtUtil.generateToken(user.getEmail(), user.getRole());

        return buildLoginResponse(token, user);
    }

    private void validateLoginData(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException(ERROR_EMAIL_REQUIRED);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(ERROR_CREDENTIAL_REQUIRED);
        }
    }

    private void validatePassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ERROR_INVALID_CREDENTIAL);
        }
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());
        return response;
    }

    private Map<String, Object> buildLoginResponse(String token, User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("status", user.getStatus());
        return response;
    }
}