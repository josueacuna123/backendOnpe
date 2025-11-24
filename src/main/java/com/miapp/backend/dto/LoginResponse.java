// Archivo: com.miapp.backend.dto.LoginResponse.java
package com.miapp.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token; // Aquí devolveremos el JWT
    private String message;
    private boolean success;
    // Opcionalmente: private String tokenType = "Bearer";
}