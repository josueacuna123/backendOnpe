// RegistrationsAdminController.java
package com.miapp.backend.controller;

import com.miapp.backend.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/registrations")
@RequiredArgsConstructor
public class RegistrationsAdminController {

    private final RegistrationService registrationService;

    // 🔹 LISTAR TODOS LOS USUARIOS REGISTRADOS (con nombres)
    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(registrationService.listRegistrations());
    }

    // 🔹 ELIMINAR UN REGISTRO POR ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!registrationService.exists(id)) {
            return ResponseEntity.badRequest().body("Registro no encontrado");
        }

        registrationService.delete(id);
        return ResponseEntity.ok("Registro eliminado");
    }
}
