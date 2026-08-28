package com.mochimexa.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/*
 * PASO 3 — JwtService
 * Centraliza la lógica para CREAR, LEER y VALIDAR JWT.
 *
 * Idea clave para clase:
 * - Header + Payload pueden decodificarse.
 * - La firma permite detectar modificaciones.
 * - La expiración limita la vigencia del token.
 */
@Service
public class JwtService {

    // Llave Base64 SOLO para fines didácticos.
    // En producción debe venir de una variable de entorno o secret manager.
    private final String secretKey;
    private final long expirationTimeMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.expiration-ms:3600000}") long expirationTimeMs
    ) {
        this.secretKey = secretKey;
        this.expirationTimeMs = expirationTimeMs;
    }

    public long getExpirationTimeMs() {

        return expirationTimeMs;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiration = new Date(System.currentTimeMillis() + expirationTimeMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver)
    {
        return resolver.apply(extractAllClaims(token));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
