package com.winderp.candidateservice.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIResponse analyserCV(String offreDescription, String cvText) {
        if (cvText == null || cvText.trim().isEmpty()) {
            return new AIResponse(0.0, "REFUSE");
        }

        String cvLimited = cvText.substring(0, Math.min(cvText.length(), 3000));

        // Prompt pour Gemini (en français)
        String prompt = """
                Compare ce CV avec cette offre d'emploi.
                
                Offre : %s
                
                CV : %s
                
                Réponds UNIQUEMENT par un objet JSON valide avec deux champs :
                - "score" : nombre entre 0 et 100 (0 = pas du tout adapté, 100 = parfaitement adapté)
                - "decision" : string, soit "ACCEPTE" soit "REFUSE"
                
                Exemple de réponse attendue :
                {"score":85,"decision":"ACCEPTE"}
                
                Ne mets rien d'autre que le JSON. Pas de texte avant ou après.
                """.formatted(offreDescription, cvLimited);

        try {
            // Création du client Gemini avec la clé API
            Client client = Client.builder()
                    .apiKey(apiKey)
                    .build();

            // Appel à Gemini 2.5 Flash
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",           // modèle
                    prompt,                       // contenu
                    null                          // configuration par défaut
            );

            // Extraction du texte généré
            String content = response.text();
            System.out.println("📝 Réponse Gemini brute: " + content);

            // Nettoyer la réponse pour n'avoir que le JSON
            String jsonContent = extractJson(content);

            // Parser le JSON
            JsonNode result = objectMapper.readTree(jsonContent);

            double score = result.path("score").asDouble(0.0);
            String decision = result.path("decision").asText("REFUSE").toUpperCase();

            // Validation
            if (!"ACCEPTE".equals(decision) && !"REFUSE".equals(decision)) {
                decision = "REFUSE";
            }
            if (score < 0) score = 0;
            if (score > 100) score = 100;

            System.out.println("✅ Score: " + score + ", Décision: " + decision);
            return new AIResponse(score, decision);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'appel Gemini: " + e.getMessage());
            e.printStackTrace();
            return new AIResponse(0.0, "REFUSE");
        }
    }

    /**
     * Extrait le premier objet JSON valide d'une chaîne.
     * Utile car Gemini peut ajouter des commentaires avant/après.
     */
    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return "{}";
    }
}