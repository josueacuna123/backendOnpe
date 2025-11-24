package com.miapp.backend.controller;

import com.miapp.backend.dto.VoteCountDTO;
import com.miapp.backend.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/stats") 
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class ElectionStatsController {

    private final VoteService voteService;

    /**
     * Obtiene el conteo de votos agrupado por candidato, filtrado por nivel geográfico.
     * Los filtros de ubicación son opcionales.
     * Ejemplo: GET /api/admin/stats/votes?categoryId=...&districtId=...
     */
    @GetMapping("/votes")
    public List<VoteCountDTO> getVoteCounts(
        @RequestParam UUID categoryId,
        @RequestParam(required = false) Integer departmentId,
        @RequestParam(required = false) Integer provinceId,
        @RequestParam(required = false) Integer districtId
    ) {
        // La lógica dinámica de filtrado está en el servicio/repositorio.
        return voteService.getVoteCounts(categoryId, departmentId, provinceId, districtId);
    }
}