package com.miapp.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Importar para manejar errores de firma
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    // 🔥 CORRECCIÓN CLAVE: Usar una cadena ASCII de 32 caracteres exactos (32 bytes)
    // Esto asegura que la longitud sea correcta para el algoritmo HS256.
    private final String SECRET = "ESTA_ES_LA_CLAVE_SECRETA_DE_32_BYTES_1234567890"; // Asegúrate de que tenga 32 caracteres
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Tiempo de expiración: 1 hora (1000 ms * 60 s * 60 min)
    private final long EXPIRATION_TIME = 1000 * 60 * 60; 

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("roles", roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) throws SignatureException {
        try {
            // Intenta parsear el token y obtener los Claims
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Manejar token expirado (opcional, podrías relanzar para que lo maneje el filtro)
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "El token ha expirado.");
        } catch (SignatureException e) {
            // 🔥 Manejar firma inválida 🔥
            throw new SignatureException("Firma JWT inválida. El token ha sido alterado o la clave secreta es incorrecta.");
        } catch (MalformedJwtException e) {
            // Manejar token mal formado
            throw new MalformedJwtException("El token no está bien formado.");
        } catch (UnsupportedJwtException e) {
            // Manejar tipo de token no soportado
            throw new UnsupportedJwtException("Token JWT no soportado.");
        } catch (Exception e) {
            // Manejar otras excepciones
            throw new RuntimeException("Error al procesar el token JWT: " + e.getMessage());
        }
    }

    public String getUsername(String token) {
        // En un entorno real, es mejor manejar aquí las excepciones lanzadas por getClaims
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            // Devuelve nulo o lanza una excepción específica si el token es inválido/expirado
            return null; 
        }
    }
}