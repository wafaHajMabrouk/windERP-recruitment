package com.winderp.candidateservice.service;

import com.winderp.candidateservice.client.AuthClient;
import com.winderp.candidateservice.models.Candidature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuthClient authClient;

    public void envoyerResultatCandidature(Candidature candidature, String emailCandidat, String nomCandidat) {
        String subject = "Résultat de votre candidature";
        String content = construireEmail(candidature, nomCandidat);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailCandidat);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("wafa.hajmabrouk396@gmail.com");

            mailSender.send(message);
            log.info("Email envoyé à {}", emailCandidat);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {}: {}", emailCandidat, e.getMessage(), e);
        }
    }

    private String construireEmail(Candidature candidature, String nomCandidat) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bonjour ").append(nomCandidat).append(",\n\n");
        sb.append("Nous vous remercions pour votre candidature à l'offre : ").append(candidature.getOffre().getTitre()).append("\n\n");

        if ("ACCEPTE".equalsIgnoreCase(candidature.getDecision())) {
            sb.append("Félicitations ! Votre candidature a été ACCEPTÉE.\n\n");
            sb.append("Score obtenu : ").append(candidature.getScore()).append("/100\n\n");
            sb.append("Un recruteur vous contactera très prochainement pour la suite du processus.\n\n");
        } else if ("REFUSE".equalsIgnoreCase(candidature.getDecision())) {
            sb.append("Nous vous remercions pour votre intérêt, mais votre candidature n'a pas été retenue.\n\n");
            sb.append("Score obtenu : ").append(candidature.getScore()).append("/100\n\n");
            sb.append("Nous vous encourageons à postuler à d'autres offres qui pourraient correspondre à votre profil.\n\n");
        } else {
            sb.append("Votre candidature est toujours en cours d'analyse.\n\n");
            sb.append("Nous vous tiendrons informé dès qu'une décision sera prise.\n\n");
        }

        sb.append("Cordialement,\n");
        sb.append("L'équipe Winderp");

        return sb.toString();
    }
}