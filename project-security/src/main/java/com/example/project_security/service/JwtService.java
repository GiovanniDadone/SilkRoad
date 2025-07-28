package com.example.project_security.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service // Indica che questa classe è un componente di servizio gestito da Spring
public class JwtService {

    // Recupera la chiave segreta per firmare/verificare i token JWT dal file
    // application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Recupera la durata del token (in millisecondi) dal file
    // application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Genera un token JWT per l'utente autenticato.
     * - Inserisce lo username come subject
     * - Inserisce i ruoli come claim
     * - Firma il token con algoritmo HS256 usando la chiave segreta
     */
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername()) // Username come subject
                .claim("roles", user.getAuthorities()) // Ruoli dell’utente come claim personalizzato
                .setIssuedAt(new Date()) // Data di emissione
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Data di scadenza
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256) // Firma del token
                .compact(); // Compatta il tutto in una stringa JWT
    }

    /**
     * Estrae lo username (subject) da un token JWT.
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Verifica se un token è valido per un determinato utente.
     * - Lo username del token deve corrispondere a quello dell'utente
     * - Il token non deve essere scaduto
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

    /**
     * Estrae tutti i claims (contenuto) da un token JWT.
     * - Serve per leggere subject, expiration, claims personalizzati, ecc.
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes()) // Imposta la chiave segreta per la verifica
                .build()
                .parseClaimsJws(token) // Parsea e verifica il token firmato
                .getBody(); // Restituisce il contenuto (claims)
    }

    /**
     * Verifica se un token JWT è scaduto.
     */
    private boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
