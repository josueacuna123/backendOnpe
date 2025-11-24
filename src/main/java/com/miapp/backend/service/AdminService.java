package com.miapp.backend.service;

import com.miapp.backend.model.Admin;
import com.miapp.backend.model.Role;
import com.miapp.backend.repository.AdminRepository;
import com.miapp.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    // -----------------------------------------------------------------
    // 1. LOGIN DE ADMIN
    // -----------------------------------------------------------------
    public Admin login(String username, String password) throws Exception {

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (!encoder.matches(password, admin.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        return admin;
    }

    // -----------------------------------------------------------------
    // 2. CREAR ADMIN (con roles)
    // -----------------------------------------------------------------
    public void create(String username, String rawPassword, List<String> roleNames) throws Exception {

        // Validar duplicado
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new Exception("El nombre de usuario ya existe");
        }

        // Buscar roles por nombre
        List<Role> roles = roleRepository.findAllByNameIn(roleNames);

        // Validar que existan TODOS los roles
        if (roles.size() != roleNames.size()) {
            throw new Exception("Uno o más nombres de rol no existen en la base de datos");
        }

        // Crear admin
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(encoder.encode(rawPassword));
        admin.setStatus("ACTIVE");
        admin.setRoles(new HashSet<>(roles));

        adminRepository.save(admin);
    }

    // -----------------------------------------------------------------
    // 3. LISTAR TODOS LOS ADMIN (para panel)
    // -----------------------------------------------------------------
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    // -----------------------------------------------------------------
    // 4. ELIMINAR ADMIN POR ID
    // -----------------------------------------------------------------
    public void delete(Integer id) throws Exception {

        if (!adminRepository.existsById(id)) {
            throw new Exception("El administrador no existe");
        }

        adminRepository.deleteById(id);
    }
}
