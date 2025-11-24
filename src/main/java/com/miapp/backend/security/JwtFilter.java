package com.miapp.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que valida el JWT recibido en cada petición y establece
 * el contexto de autenticación en Spring Security.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Verificar formato correcto del header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Extraer token sin "Bearer "
                String token = authHeader.substring(7);

                // Extraer claims (usuario + roles)
                Claims claims = jwtUtil.getClaims(token);

                // Obtener roles del token
                List<String> roles = claims.get("roles", List.class);

                // Convertir roles a autorizaciones Spring
                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(), // username
                                null,                 // no enviamos password
                                authorities           // roles
                        );

                // Establecer autenticación en el contexto
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
            // --------------------------------------
            // MANEJO PROFESIONAL DE ERRORES JWT
            // --------------------------------------

            catch (ExpiredJwtException e) {
                System.err.println("⛔ TOKEN EXPIRADO: " + e.getMessage());
            }
            catch (SignatureException e) {
                System.err.println("⛔ FIRMA INVÁLIDA EN TOKEN: " + e.getMessage());
            }
            catch (Exception e) {
                System.err.println("⛔ ERROR AL PROCESAR TOKEN: " + e.getMessage());
            }
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}
