package com.miapp.backend.config;

import com.miapp.backend.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // =====================================================
    // 🔥 CORS CONFIG — ESTA ES LA QUE ARREGLA EL ERROR
    // =====================================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:5173",
                "https://frontend-onpe.vercel.app"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // IMPORTANTE

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // =====================================================
    // 🔐 SECURITY FILTER CHAIN
    // =====================================================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // ============================
                // LOGIN
                // ============================
                .requestMatchers(HttpMethod.POST, "/api/admin/login").permitAll()

                // ============================
                // PARTIDOS (solo GET público)
                // ============================
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/partidos").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/partidos/").permitAll()

                // CRUD protegido
                .requestMatchers("/api/partidos/**")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_CANDIDATOS")

                // ============================
                // BLOQUEO DE CATEGORÍAS
                // ============================
                .requestMatchers(HttpMethod.PATCH, "/api/admin/categories/*/lock")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_VOTACIONES")

                // ============================
                // OTRAS RUTAS ADMIN
                // ============================
                .requestMatchers("/api/admin/departments/**")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_VOTACIONES")
                .requestMatchers("/api/admin/provinces/**")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_VOTACIONES")
                .requestMatchers("/api/admin/districts/**")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_VOTACIONES")

                .requestMatchers("/api/admin/parties/**")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_CANDIDATOS")

                .requestMatchers("/api/admin/manage/**")
                    .hasAuthority("ROLE_GENERAL")

                .requestMatchers("/api/admin/registrations/**")
                    .hasAuthority("ROLE_GENERAL")

                // ============================
                // SEMIPÚBLICAS
                // ============================
                .requestMatchers("/api/public/**")
                    .hasAnyAuthority("ROLE_GENERAL", "ROLE_CANDIDATOS", "ROLE_VOTACIONES")

                // ============================
                // PÚBLICAS
                // ============================
                .requestMatchers(HttpMethod.GET, "/api/dni/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/locations/**").permitAll()
                .requestMatchers("/photos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/vote/candidates/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/vote/submit").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
