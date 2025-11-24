package com.miapp.backend.controller;

import com.miapp.backend.model.Admin;
import com.miapp.backend.model.Role; // 🔥 ¡AÑADE ESTA LÍNEA! 🔥
import com.miapp.backend.security.JwtUtil;
import com.miapp.backend.service.AdminService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.miapp.backend.repository.RoleRepository; // ¡Importa esto!

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository; // 🔥 1. Inyectar RoleRepository

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Admin admin = adminService.login(req.getUsername(), req.getPassword());

            // Convertir roles del admin (Set<Role>) a List<String>
            List<String> roleNames = admin.getRoles()
                    .stream()
                    .map(r -> r.getName())   // ROLE_GENERAL, ROLE_AUDITOR, etc.
                    .collect(Collectors.toList());

            // Generar JWT correcto
            String token = jwtUtil.generateToken(admin.getUsername(), roleNames);

            return ResponseEntity.ok(Map.of(
                "token", token,
                "username", admin.getUsername(),
                "roles", roleNames
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        // Asumiendo que Role es importado
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
