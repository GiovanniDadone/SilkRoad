package com.example.project_security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // <-- **1. ABILITA CORS QUI**
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ... (vedi il punto 3 per le correzioni qui)
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ... (il tuo bean authManager rimane invariato)

    // **2. DECOMMENTA E USA QUESTO BEAN PER CONFIGURARE CORS**
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Applica CORS a tutti gli endpoint /api/
                        .allowedOrigins("http://localhost:3000") // Permetti richieste dal tuo frontend React
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metodi permessi
                        .allowedHeaders("*") // Permetti tutti gli header
                        .allowCredentials(true);
            }
        };
    }

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