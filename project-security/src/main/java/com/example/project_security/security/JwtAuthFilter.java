package com.example.project_security.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.project_security.service.CustomUserDetailsService;
import com.example.project_security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro che intercetta ogni richiesta HTTP e verifica la presenza di un token
 * JWT valido.
 * Se il token è presente e valido, autentica l’utente impostando lo UserDetails
 * nel SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // Costruttore con injection dei servizi necessari
    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Metodo eseguito automaticamente ad ogni richiesta HTTP.
     * Controlla se c'è un header Authorization con un token JWT valido.
     * Se sì, autentica l'utente associato e imposta il contesto di sicurezza.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Estrae l'header Authorization dalla richiesta
        String authHeader = request.getHeader("Authorization");

        // Se l'header è nullo o non inizia con "Bearer ", salta il filtro e prosegue
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Estrae il token JWT eliminando il prefisso "Bearer "
        String token = authHeader.substring(7);

        // Estrae lo username (subject) dal token
        String username = jwtService.extractUsername(token);

        // Se lo username è presente e l'utente non è già autenticato
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carica l'utente dal database (o da un servizio)
            UserDetails user = userDetailsService.loadUserByUsername(username);

            // Verifica se il token è valido per l'utente caricato
            if (jwtService.isTokenValid(token, user)) {
                // Crea un oggetto di autenticazione con ruoli e dettagli dell’utente
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());

                // Aggiunge ulteriori dettagli della richiesta (IP, sessione, ecc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Imposta l'utente autenticato nel SecurityContext di Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua con la catena dei filtri
        filterChain.doFilter(request, response);
    }
}
