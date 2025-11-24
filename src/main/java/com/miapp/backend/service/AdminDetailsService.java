// Archivo: com.miapp.backend.service.AdminDetailsService.java
package com.miapp.backend.service;

import com.miapp.backend.model.Admin;
import com.miapp.backend.repository.AdminRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Importar las clases de logging (necesarias para la depuración)

import java.util.stream.Collectors;

@Service
public class AdminDetailsService implements UserDetailsService {

    // Instancia del logger para la depuración


    private final AdminRepository adminRepository;

    // CONSTRUCTOR EXPLÍCITO (sustituye a @RequiredArgsConstructor)
    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
        
        // *****************************************************************
        // *** LÍNEA DE DEPURACIÓN (para verificar el hash leído de la DB) ***
        // *****************************************************************
        // Construye y devuelve el objeto User de Spring Security
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword()) // Usará el valor que acabas de loggear
                .disabled(!"ACTIVE".equals(admin.getStatus()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                // Mapea los roles a GrantedAuthority
                .authorities(admin.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) 
                        .collect(Collectors.toList()))
                .build();
    }
}