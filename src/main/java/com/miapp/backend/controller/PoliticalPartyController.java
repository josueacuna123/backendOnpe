package com.miapp.backend.controller;

import com.miapp.backend.model.PoliticalParty;
import com.miapp.backend.service.PoliticalPartyService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PoliticalPartyController {

    private final PoliticalPartyService service;

    public PoliticalPartyController(PoliticalPartyService service) {
        this.service = service;
    }

    // ====================================================
    // GET - Listar partidos
    // ====================================================
    @GetMapping
    public List<PoliticalParty> listar() {
        return service.getAll();
    }

    // ====================================================
    // POST - Crear partido con logo opcional
    // ====================================================
    @PostMapping("/create")
    public PoliticalParty crear(
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile logo
    ) {
        try {
            String logoPath = null;

            if (logo != null && !logo.isEmpty()) {
                String folder = "uploads/partidos/";
                Files.createDirectories(Paths.get(folder));

                String filename = UUID.randomUUID() + "_" + logo.getOriginalFilename();
                String fullPath = folder + filename;

                Files.copy(logo.getInputStream(), Paths.get(fullPath));
                logoPath = fullPath;
            }

            PoliticalParty party = new PoliticalParty();
            party.setName(name);
            party.setLogoPath(logoPath);

            return service.create(party);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear partido: " + e.getMessage());
        }
    }

    // ====================================================
    // PUT - Actualizar partido (+ logo opcional)
    // ====================================================
    @PutMapping("/update/{id}")
    public PoliticalParty actualizar(
            @PathVariable UUID id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile logo
    ) {
        try {
            String logoPath = null;

            if (logo != null && !logo.isEmpty()) {
                String folder = "uploads/partidos/";
                Files.createDirectories(Paths.get(folder));

                String filename = UUID.randomUUID() + "_" + logo.getOriginalFilename();
                String fullPath = folder + filename;

                Files.copy(logo.getInputStream(), Paths.get(fullPath));
                logoPath = fullPath;
            }

            PoliticalParty party = new PoliticalParty();
            party.setName(name);
            party.setLogoPath(logoPath);

            return service.update(id, party);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar partido: " + e.getMessage());
        }
    }

    // ====================================================
    // DELETE - Eliminar partido
    // ====================================================
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable UUID id) {
        service.delete(id);
    }
}
