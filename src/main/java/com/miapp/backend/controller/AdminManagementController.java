package com.miapp.backend.controller;

import com.miapp.backend.service.AdminService; // 🔥 Importar el servicio
import com.miapp.backend.model.Admin;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/manage")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_GENERAL')") // 🔒 Solo GENERAL
public class AdminManagementController {

    // 🔥 SOLO INYECTAMOS EL SERVICIO 🔥
    private final AdminService adminService; 

    // Crear nuevo admin
    // ... dentro de createAdmin() ...

// 1. Asegúrate de que CreateAdminRequest ahora tenga List<String> roles
// 2. Llama al método getRoles() en el controlador

    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminRequest req) {

        try {
            // 🔥 CORRECCIÓN: CAMBIAR getRoleIds() por getRoles() 🔥
            // El servicio ahora DEBE aceptar List<String>
            adminService.create(req.getUsername(), req.getPassword(), req.getRoles()); 
            
            return ResponseEntity.ok("Administrador creado");

        } catch (Exception e) {
            // ... manejo de errores ...
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/list")
    public ResponseEntity<List<Admin>> listAdmins() {
        List<Admin> admins = adminService.findAll();
        return ResponseEntity.ok(admins);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
        try {
            adminService.delete(id);
            return ResponseEntity.ok("Administrador eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// Clase Request (DEBE ESTAR FUERA DEL CONTROLADOR O MARCARSE COMO 'static')
@Data
class CreateAdminRequest {
    private String username;
    private String password;
    private List<String> roles;
}