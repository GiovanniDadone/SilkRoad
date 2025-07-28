package com.example.project_security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.project_security.service.CustomUserDetailsService;

/**
 * Configura la sicurezza dell'applicazione:
 * - Imposta il filtro JWT per le richieste
 * - Definisce le regole di autorizzazione
 * - Configura l'AuthenticationManager con UserDetailsService personalizzato
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Costruttore con injection del filtro JWT, UserDetailsService e
    // PasswordEncoder
    public SecurityConfig(JwtAuthFilter jwtFilter, CustomUserDetailsService userDetailsService,
            PasswordEncoder encoder) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = encoder;
    }

    /**
     * Definisce la catena di sicurezza HTTP:
     * - Disabilita CSRF
     * - Imposta la sessione in modalitÃ stateless (JWT)
     * - Applica regole di autorizzazione
     * - Registra il filtro JWT prima del filtro di autenticazione standard
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Disabilita CSRF (dato che non usiamo cookie/sessione)
                .csrf(csrf -> csrf.disable())

                // Imposta la gestione delle sessioni come stateless (non viene usata la
                // sessione HTTP)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Definisce le regole di accesso alle rotte
                .authorizeHttpRequests(auth -> auth
                        // Le rotte di login e pubbliche non richiedono autenticazione
                        .requestMatchers("/auth/**", "/public/**").permitAll()

                        // Le rotte /admin/** richiedono il ruolo ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Tutte le altre richieste devono essere autenticate
                        .anyRequest().authenticated())

                // Aggiunge il filtro JWT prima del filtro standard di autenticazione
                // username/password
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Costruisce e restituisce la catena
                .build();
    }

    /**
     * Configura l'AuthenticationManager da usare manualmente o nel controller.
     * Usa il servizio utenti personalizzato e il password encoder scelto (es.
     * BCrypt).
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authBuilder.build();
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    // return new WebMvcConfigurer() {
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // registry.addMapping("/**")
    // .allowedOrigins("http://localhost:3000")
    // .allowedMethods("GET", "POST", "PUT", "DELETE")
    // .allowCredentials(true);
    // }
    // };
    // }
}