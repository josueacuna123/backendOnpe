package com.miapp.backend.controller;

import com.miapp.backend.model.Registrations;
import com.miapp.backend.repository.RegistrationsRepository;
import com.miapp.backend.service.DniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dni")
@RequiredArgsConstructor
public class DniController {

    private final RegistrationsRepository registrationsRepo;
    private final DniService dniService;

    @GetMapping("/{dni}")
    public ResponseEntity<?> consultarDni(@PathVariable String dni) {

        try {
            // 1️⃣ BUSCAR EN LA BASE DE DATOS
            Optional<Registrations> regOpt = registrationsRepo.findByDni(dni);

            if (regOpt.isPresent()) {
                Registrations reg = regOpt.get();

                // 2️⃣ DEVOLVER DATOS COMPLETOS YA REGISTRADOS
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", Map.of(
                                "nombre_completo", reg.getFull_name(),
                                "departamento", reg.getDepartment_id(),
                                "provincia", reg.getProvince_id(),
                                "distrito", reg.getDistrict_id()
                        )
                ));
            }

            // 3️⃣ SI NO EXISTE EN BD → consultar API Perú
            Map<String, Object> resp = dniService.getDniData(dni);
            boolean success = (resp.get("success") instanceof Boolean b && b);

            if (!success) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "DNI no válido o no encontrado"
                ));
            }

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error interno",
                    "detail", e.getMessage()
            ));
        }
    }
}
