package com.winderp.candidateservice.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
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
            return new AIResponse(30.0, "EN_ATTENTE");
        }

        String cvLimited = cvText.substring(0, Math.min(cvText.length(), 8000));

        String prompt = """
                Tu es un recruteur professionnel, objectif et juste.
                
                Analyse ce CV par rapport à l'offre d'emploi suivante :

                === OFFRE ===
                %s

                === CV DU CANDIDAT ===
                %s

                Réponds **UNIQUEMENT** avec un objet JSON valide et complet, sans aucun texte avant ou après, sans commentaires :

                {
                  "score": nombre entier entre 0 et 100,
                  "decision": "ACCEPTE" ou "REFUSE" ou "EN_ATTENTE",
                  "raison": "explication courte en une phrase maximum"
                }

                Règles importantes :
                - Sois objectif mais pas trop sévère.
                - Si le candidat a des compétences proches ou transférables → score >= 60 et "ACCEPTE".
                - Ne mets "REFUSE" que si le profil est clairement incompatible.
                - Toujours inclure le champ "raison" complet.
                """.formatted(offreDescription, cvLimited);

        // Modèles valides en avril 2026 (1.5-flash est mort)
        String[] models = {
                "gemini-2.5-flash",
                "gemini-2.5-flash-lite",
                "gemini-2.5-pro"          // plus lent mais souvent plus fiable pour le JSON
        };

        for (String model : models) {
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    Client client = Client.builder().apiKey(apiKey).build();

                    GenerateContentConfig config = GenerateContentConfig.builder()
                            .temperature(0.1f)          // encore plus bas pour forcer la structure
                            .maxOutputTokens(1000)
                            .responseMimeType("application/json")
                            .safetySettings(List.of(
                                    SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_HARASSMENT).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build(),
                                    SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_HATE_SPEECH).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build(),
                                    SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_SEXUALLY_EXPLICIT).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build(),
                                    SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_DANGEROUS_CONTENT).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build()
                            ))
                            .build();

                    System.out.println("Tentative " + attempt + " avec modèle : " + model);

                    GenerateContentResponse response = client.models.generateContent(model, prompt, config);

                    String content = response.text().trim();
                    System.out.println(" [" + model + "] Réponse brute : " + content);

                    String jsonContent = extractJson(content);
                    JsonNode result = objectMapper.readTree(jsonContent);

                    double score = result.path("score").asDouble(50.0);
                    String decision = result.path("decision").asText("EN_ATTENTE").toUpperCase().trim();
                    String raison = result.path("raison").asText("Analyse terminée");

                    if (!List.of("ACCEPTE", "REFUSE", "EN_ATTENTE").contains(decision)) {
                        decision = "EN_ATTENTE";
                    }
                    score = Math.max(0.0, Math.min(100.0, score));

                    System.out.println(" [" + model + "] Succès → Score: " + score + " | Decision: " + decision + " | Raison: " + raison);

                    return new AIResponse(score, decision);

                } catch (Exception e) {
                    System.err.println(" [" + model + "] Tentative " + attempt + "/3 échouée : " + e.getMessage());
                    if (attempt < 3) {
                        try { Thread.sleep(1500L * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    }
                }
            }
        }

        System.err.println("⚠ Tous les modèles ont échoué → fallback EN_ATTENTE");
        return new AIResponse(50.0, "EN_ATTENTE");
    }


    private String extractJson(String text) {

        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            String jsonCandidate = text.substring(start, end + 1).trim();

            if (!jsonCandidate.endsWith("}")) {
                jsonCandidate += "}";
            }
            return jsonCandidate;
        }
        return "{\"score\":50,\"decision\":\"EN_ATTENTE\",\"raison\":\"Parsing échoué - réponse incomplète\"}";
    }
}