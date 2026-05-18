package com.winderp.authentification.services;

import com.winderp.authentification.models.User;
import com.winderp.authentification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }



    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    public User update(Long id, User updatedUser) {
        User existing = getById(id);
        // update fields
        existing.setNom(updatedUser.getNom());
        existing.setPrenom(updatedUser.getPrenom());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        existing.setDepartement(updatedUser.getDepartement());
        existing.setNiveauResponsabilite(updatedUser.getNiveauResponsabilite());
        existing.setAdresse(updatedUser.getAdresse());
        existing.setCompetences(updatedUser.getCompetences());
        existing.setNiveauExperience(updatedUser.getNiveauExperience());
        existing.setEntreprise(updatedUser.getEntreprise());
        existing.setPoste(updatedUser.getPoste());
        existing.setSiteEntreprise(updatedUser.getSiteEntreprise());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        User existing = getById(id);
        repository.delete(existing);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));
    }

    // Batch fetch (optional but recommended)
    public List<User> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Change the password for a user.
     * @param id the user ID
     * @param oldPassword current password
     * @param newPassword new password to set
     * @throws ResponseStatusException if old password does not match
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = getById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ancien mot de passe incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }
    public User approveUser(Long id) {
        User user = getById(id);
        user.setStatus("APPROVED");
        return repository.save(user);
    }

    public User rejectUser(Long id) {
        User user = getById(id);
        user.setStatus("REJECTED");
        return repository.save(user);
    }

}