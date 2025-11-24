package com.miapp.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; // Importamos ResponseEntity
import org.springframework.web.bind.annotation.*;
import com.miapp.backend.service.RegistrationService;
import com.miapp.backend.dto.VoterRegistrationDTO;
import java.util.List;

// Archivo: VoterAdminController.java (CORREGIDO)

@RestController
@RequestMapping("/api/admin/voters")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class VoterAdminController {

    private final RegistrationService registrationService;

    /**
     * Obtiene una lista de todos los registros de votantes, aplicando filtros opcionales.
     */
    @GetMapping // Ruta: /api/admin/voters
    public ResponseEntity<List<VoterRegistrationDTO>> getAllVoters(
            // Filtros de texto (String)
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryName,
            // Filtros numéricos (Integer)
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) Integer districtId) {
        
        // Delegamos la tarea de filtrado al servicio con todos los parámetros
        List<VoterRegistrationDTO> filteredVoters = registrationService.findAllVoterRegistrationsWithFilters(
            dni, 
            name, 
            categoryName, 
            departmentId, 
            provinceId, 
            districtId
        );
        
        return ResponseEntity.ok(filteredVoters);
    }
}
