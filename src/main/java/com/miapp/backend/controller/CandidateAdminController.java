// Archivo: com.miapp.backend.controller.CandidateAdminController.java

package com.miapp.backend.controller;

// Importaciones necesarias
import com.miapp.backend.model.Candidate;
import com.miapp.backend.dto.CreateCandidateRequest;
import com.miapp.backend.dto.UpdateCandidateRequest;
import com.miapp.backend.service.CandidateService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/candidatos")
public class CandidateAdminController {

    @Autowired
    private CandidateService candidateService;

    // CREAR: POST /api/admin/candidatos
    @PostMapping
    public ResponseEntity<Candidate> createCandidate(@RequestBody CreateCandidateRequest req) {
        try {
            Candidate nuevoCandidato = candidateService.createCandidate(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCandidato);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // LEER/FILTRAR: GET /api/admin/candidatos?search=nombre
    @GetMapping
    public ResponseEntity<List<Candidate>> getCandidates(@RequestParam(required = false) String search) {
        List<Candidate> candidates = candidateService.getCandidatesByFilter(search);
        return ResponseEntity.ok(candidates);
    }

    // ACTUALIZAR: PUT /api/admin/candidatos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable UUID id, @RequestBody UpdateCandidateRequest req) {
        try {
            Candidate updated = candidateService.updateCandidate(id, req);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ELIMINAR: DELETE /api/admin/candidatos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable UUID id) {
        try {
            candidateService.deleteCandidate(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}