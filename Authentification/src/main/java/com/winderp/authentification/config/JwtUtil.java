package com.winderp.authentification.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public final class JwtUtil {

    // Clé par défaut : 32 caractères → 256 bits
    private static final String DEFAULT_SECRET = "TaCleTresSecreteAuMoins32Caracts";
    private static final long DEFAULT_EXPIRATION = 86400000L;

    private static Key signingKey;
    private static long expiration = DEFAULT_EXPIRATION;

    static {
        signingKey = Keys.hmacShaKeyFor(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Constructeur privé pour cacher le constructeur public implicite.
     * Cette classe est une classe utilitaire avec uniquement des méthodes statiques.
     */
    private JwtUtil() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    /**
     * À appeler depuis une classe @Configuration pour injecter les valeurs du properties
     */
    public static void init(String jwtSecret, long jwtExpiration) {
        if (jwtSecret != null && !jwtSecret.isEmpty()) {
            signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
        expiration = jwtExpiration;
    }

    private static Key getSigningKey() {
        return signingKey;
    }

    public static String generateToken(String email, String role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email ne peut pas être null ou vide");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Le rôle ne peut pas être null ou vide");
        }

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}