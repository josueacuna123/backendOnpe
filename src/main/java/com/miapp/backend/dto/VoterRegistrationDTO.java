// Archivo: VoterRegistrationDTO.java (Ejemplo de DTO completo)

package com.miapp.backend.dto;

import java.time.LocalDateTime;

// Usaremos Lombok para simplificar los getters/setters
import lombok.Data; 

@Data // Anotación de Lombok para Getters, Setters, etc.
public class VoterRegistrationDTO {
    private String voteId;
    // --- Datos de Registro de Votante (EXISTENTES) ---
    private String registrationId;
    private String voterDNI;
    private String voteStatus;
    private LocalDateTime registrationDate;

    // --- Datos de Voto y Candidato (NUEVOS) ---
    private String candidateId;
    private String candidateFullName; // Nombre del candidato
    private String categoryName;
    private String voterName;      // Nombre de la categoría (ej: Presidencial)

    // --- Datos de Partido (Requiere Doble JOIN) ---
    private String partyId;
    private String partyName;         // Nombre del partido

    // --- Datos de Ubicación (NUEVOS) ---
    private Long departmentId;
    private String departmentName;    // Nombre del departamento
    private Long provinceId;
    private String provinceName;      // Nombre de la provincia
    private Long districtId;
    private String districtName;      // Nombre del distrito

    // ... (Posibles constructores si no usas @Data)
    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }
}