package com.winderp.authentification.Repository;

import com.winderp.authentification.Models.Admin;
import com.winderp.authentification.Models.Candidate;
import com.winderp.authentification.Models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_WithCandidate() {
        Candidate candidate = new Candidate();
        candidate.setEmail("candidate@test.com");
        candidate.setPassword("password123");
        candidate.setNom("Dupont");
        candidate.setPrenom("Jean");
        candidate.setRole("CANDIDATE");
        candidate.setStatus("APPROVED");

        entityManager.persistAndFlush(candidate);

        var found = userRepository.findByEmail("candidate@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("candidate@test.com");
        assertThat(found.get().getRole()).isEqualTo("CANDIDATE");
    }

    @Test
    void testFindByEmail_WithAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword("admin123");
        admin.setNom("Admin");
        admin.setPrenom("System");
        admin.setRole("ADMIN");
        admin.setStatus("APPROVED");

        entityManager.persistAndFlush(admin);

        var found = userRepository.findByEmail("admin@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("admin@test.com");
        assertThat(found.get().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void testCheckEmailExists_True() {
        Candidate user = new Candidate();
        user.setEmail("exists@example.com");
        user.setPassword("password");
        user.setRole("CANDIDATE");
        user.setStatus("PENDING");

        entityManager.persistAndFlush(user);

        boolean emailExists = userRepository.findByEmail("exists@example.com").isPresent();
        assertThat(emailExists).isTrue();
    }

    @Test
    void testCheckEmailExists_False() {
        boolean emailExists = userRepository.findByEmail("nonexistent@example.com").isPresent();
        assertThat(emailExists).isFalse();
    }

    @Test
    void testFindByEmail_NotFound() {
        var found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }
}