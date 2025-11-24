package com.miapp.backend.dto;

// Archivo: com.miapp.backend.dto.UpdateCandidateRequest.java

import java.util.UUID;
import lombok.Data;

@Data
public class UpdateCandidateRequest {
    private String fullName;
    private UUID partyId;
    private UUID categoryId;
    private String photoPath;
    
    // 🔥 CORRECCIÓN: DEBEN SER INTEGER (int) para coincidir con JpaRepository<Entity, Integer>
    private Integer departmentId; 
    private Integer provinceId;
    private Integer districtId;
}
