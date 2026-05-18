package com.winderp.authentification.services;

import com.winderp.authentification.models.*;           // ← models (minuscule)
import com.winderp.authentification.repository.UserRepository; // ← repository (minuscule)
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

    // Constantes pour les statuts
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";

    // Constantes pour les rôles
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CANDIDATE = "CANDIDATE";
    private static final String ROLE_RH = "RH";
    private static final String ROLE_RECRUTEUR = "RECRUTEUR";

    // Constantes pour les messages d'erreur
    private static final String ERROR_EMAIL_REQUIRED = "Email requis";
    private static final String ERROR_CREDENTIAL_REQUIRED = "Mot de passe requis";
    private static final String ERROR_INCOMPLETE_DATA = "Données d'inscription incomplètes";
    private static final String ERROR_EMAIL_ALREADY_USED = "Email déjà utilisé";
    private static final String ERROR_INVALID_ROLE = "Rôle invalide: ";
    private static final String ERROR_USER_NOT_FOUND = "Utilisateur non trouvé";
    private static final String ERROR_INVALID_CREDENTIAL = "Mot de passe incorrect";
    private static final String ERROR_ACCOUNT_NOT_APPROVED = "Compte non validé par admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> register(User data) {
        validateRegistrationData(data);
        checkEmailNotExists(data.getEmail());

        String encodedPassword = passwordEncoder.encode(data.getPassword());
        String role = data.getRole().toUpperCase();

        User userToSave = createUserByRole(data, encodedPassword, role);

        User savedUser = userRepository.save(userToSave);
        return buildUserResponse(savedUser);
    }

    private void validateRegistrationData(User data) {
        if (data == null || data.getEmail() == null || data.getPassword() == null || data.getRole() == null) {
            throw new BusinessException(ERROR_INCOMPLETE_DATA);
        }
    }

    private void checkEmailNotExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ERROR_EMAIL_ALREADY_USED);
        }
    }

    private User createUserByRole(User data, String encodedPassword, String role) {
        switch (role) {
            case ROLE_ADMIN:
                return createAdmin(data, encodedPassword, role);
            case ROLE_CANDIDATE:
                return createCandidate(data, encodedPassword, role);
            case ROLE_RH:
                return createRh(data, encodedPassword, role);
            case ROLE_RECRUTEUR:
                return createRecruteur(data, encodedPassword, role);
            default:
                throw new BusinessException(ERROR_INVALID_ROLE + role);
        }
    }

    private Admin createAdmin(User data, String encodedPassword, String role) {
        Admin admin = new Admin();
        if (data.getDepartement() != null) {
            admin.setDepartement(data.getDepartement());
        }
        copyCommon(data, admin, encodedPassword, role);
        admin.setStatus(STATUS_APPROVED);
        return admin;
    }

    private Candidate createCandidate(User data, String encodedPassword, String role) {
        Candidate candidate = new Candidate();
        if (data.getAdresse() != null) candidate.setAdresse(data.getAdresse());
        if (data.getCompetences() != null) candidate.setCompetences(data.getCompetences());
        if (data.getNiveauExperience() != null) candidate.setNiveauExperience(data.getNiveauExperience());
        copyCommon(data, candidate, encodedPassword, role);
        candidate.setStatus(STATUS_PENDING);
        return candidate;
    }

    private RH createRh(User data, String encodedPassword, String role) {
        RH rh = new RH();
        if (data.getDepartement() != null) rh.setDepartement(data.getDepartement());
        if (data.getNiveauResponsabilite() != null) rh.setNiveauResponsabilite(data.getNiveauResponsabilite());
        copyCommon(data, rh, encodedPassword, role);
        rh.setStatus(STATUS_PENDING);
        return rh;
    }

    private Recruteur createRecruteur(User data, String encodedPassword, String role) {
        Recruteur recruteur = new Recruteur();
        if (data.getEntreprise() != null) recruteur.setEntreprise(data.getEntreprise());
        if (data.getPoste() != null) recruteur.setPoste(data.getPoste());
        if (data.getSiteEntreprise() != null) recruteur.setSiteEntreprise(data.getSiteEntreprise());
        copyCommon(data, recruteur, encodedPassword, role);
        recruteur.setStatus(STATUS_PENDING);
        return recruteur;
    }

    private void copyCommon(User source, User target, String password, String role) {
        target.setEmail(source.getEmail());
        target.setNom(source.getNom());
        target.setPrenom(source.getPrenom());
        target.setPassword(password);
        target.setRole(role);
    }

    public Map<String, Object> login(String email, String password) {
        validateLoginData(email, password);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ERROR_USER_NOT_FOUND));

        validatePassword(password, user);
        validateAccountStatus(user);

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

    private void validateAccountStatus(User user) {
        if (!ROLE_ADMIN.equals(user.getRole()) && !STATUS_APPROVED.equals(user.getStatus())) {
            throw new BusinessException(ERROR_ACCOUNT_NOT_APPROVED);
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