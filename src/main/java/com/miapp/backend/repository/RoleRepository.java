package com.miapp.backend.repository;

import com.miapp.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List; // Necesitas importar List

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    Optional<Role> findByName(String name);
    
    // 🔥 AÑADE ESTE MÉTODO 🔥
    // Permite buscar todos los objetos Role cuyos nombres están contenidos en la lista 'names'
    List<Role> findAllByNameIn(List<String> names); 
}