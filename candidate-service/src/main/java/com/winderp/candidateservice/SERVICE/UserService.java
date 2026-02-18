package com.winderp.candidateservice.SERVICE;

import com.winderp.candidateservice.Models.User;
import com.winderp.candidateservice.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // ================= CREATE =================
    public User create(User user) {
        return repository.save(user);
    }

    // ================= READ ALL =================
    public List<User> getAll() {
        return repository.findAll();
    }

    // ================= READ BY ID =================
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // ================= UPDATE =================
    public User update(Long id, User updatedUser) {
        User existing = getById(id);

        existing.setNom(updatedUser.getNom());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());

        return repository.save(existing);
    }

    // ================= DELETE =================
    public void delete(Long id) {
        User existing = getById(id);
        repository.delete(existing);
    }

    // ================= SEARCH BY EMAIL =================
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
