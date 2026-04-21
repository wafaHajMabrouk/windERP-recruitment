package com.winderp.authentification.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Clé par défaut : 32 caractères → 256 bits
    private static final String DEFAULT_SECRET = "TaCleTresSecreteAuMoins32Caracts"; // ajout d'un 's' final
    private static final long DEFAULT_EXPIRATION = 86400000L;

    private static Key signingKey;
    private static long expiration = DEFAULT_EXPIRATION;

    static {
        signingKey = Keys.hmacShaKeyFor(DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8));
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
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}