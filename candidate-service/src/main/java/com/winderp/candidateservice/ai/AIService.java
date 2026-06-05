package com.winderp.candidateservice.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AIService {

    private static final String DEFAULT_DECISION = "EN_ATTENTE";
    private static final String[] MODELS = {"gemini-2.5-flash", "gemini-2.5-flash-lite", "gemini-2.5-pro"};
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BASE_DELAY_MS = 1500L;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIResponse analyserCV(String offreDescription, String cvText) {
        if (cvText == null || cvText.trim().isEmpty()) {
            log.warn("CV vide ou null, retour score 30.0 et décision {}", DEFAULT_DECISION);
            return new AIResponse(30.0, DEFAULT_DECISION);
        }

        String cvLimited = limitCvLength(cvText);
        String prompt = buildPrompt(offreDescription, cvLimited);

        for (String model : MODELS) {
            AIResponse response = tryModelWithRetries(model, prompt);
            if (response != null) {
                return response;
            }
        }

        log.error("Tous les modèles ont échoué → fallback à {}", DEFAULT_DECISION);
        return new AIResponse(50.0, DEFAULT_DECISION);
    }

    // ---------- Méthodes privées (réduction de complexité) ----------

    private String limitCvLength(String cvText) {
        return cvText.substring(0, Math.min(cvText.length(), 8000));
    }

    private String buildPrompt(String offreDescription, String cvLimited) {
        return """
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
    }

    private AIResponse tryModelWithRetries(String model, String prompt) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.debug("Tentative {} avec modèle {}", attempt, model);
                String responseContent = callGemini(model, prompt);
                log.debug("[{}] Réponse brute : {}", model, responseContent);

                AIResponse parsed = parseResponse(responseContent);
                log.info("[{}] Succès → Score: {} | Décision: {}", model, parsed.getScore(), parsed.getDecision());
                return parsed;

            } catch (Exception e) {
                log.warn("[{}] Tentative {}/{} échouée : {}", model, attempt, MAX_RETRIES, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    sleep(RETRY_BASE_DELAY_MS * attempt);
                }
            }
        }
        return null;
    }

    private String callGemini(String model, String prompt) throws Exception {
        Client client = Client.builder().apiKey(apiKey).build();
        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.1f)
                .maxOutputTokens(1000)
                .responseMimeType("application/json")
                .safetySettings(List.of(
                        SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_HARASSMENT).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build(),
                        SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_HATE_SPEECH).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build(),
                        SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_SEXUALLY_EXPLICIT).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build(),
                        SafetySetting.builder().category(HarmCategory.Known.HARM_CATEGORY_DANGEROUS_CONTENT).threshold(HarmBlockThreshold.Known.BLOCK_NONE).build()
                ))
                .build();
        GenerateContentResponse response = client.models.generateContent(model, prompt, config);
        return response.text().trim();
    }

    private AIResponse parseResponse(String content) {
        String jsonContent = extractJson(content);
        try {
            JsonNode result = objectMapper.readTree(jsonContent);
            double score = result.path("score").asDouble(50.0);
            String decision = result.path("decision").asText(DEFAULT_DECISION).toUpperCase().trim();
            // Validation et normalisation
            if (!List.of("ACCEPTE", "REFUSE", DEFAULT_DECISION).contains(decision)) {
                decision = DEFAULT_DECISION;
            }
            score = Math.max(0.0, Math.min(100.0, score));
            return new AIResponse(score, decision);
        } catch (Exception e) {
            log.error("Erreur parsing JSON : {}", e.getMessage());
            return new AIResponse(50.0, DEFAULT_DECISION);
        }
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
        return "{\"score\":50,\"decision\":\"" + DEFAULT_DECISION + "\",\"raison\":\"Parsing échoué - réponse incomplète\"}";
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Sleep interrompu");
        }
    }
}