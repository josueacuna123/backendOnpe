// Archivo: com.miapp.backend.dto.LoginRequest.java

package com.miapp.backend.dto;

import lombok.Data; // Incluye @Getter, @Setter, @ToString, @EqualsAndHashCode

@Data // Usa @Data para tener todos los getters/setters
public class LoginRequest {
    private String username; // Lombok genera getUsername()
    private String password; // Lombok genera getPassword()
}