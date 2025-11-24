package com.miapp.backend.dto;

// Archivo: com.miapp.backend.dto.CreateCandidateRequest.java

// Archivo: com.miapp.backend.dto.CreateCandidateRequest.java (REVISAR TIPOS)

// Archivo: com.miapp.backend.dto.CreateCandidateRequest.java (CORREGIDO)

import java.util.UUID;
import lombok.Data;

@Data
public class CreateCandidateRequest {
    private String fullName;
    private UUID partyId;
    private UUID categoryId;
    private String photoPath;
    
    // 🔥 CORRECCIÓN: DEBEN SER INTEGER (int) para coincidir con JpaRepository<Entity, Integer>
    private Integer departmentId; 
    private Integer provinceId;
    private Integer districtId;
}
// Repite esta corrección en UpdateCandidateRequest.java