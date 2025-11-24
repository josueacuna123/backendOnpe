package com.miapp.backend.repository;

import com.miapp.backend.model.Registrations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistrationsRepository extends JpaRepository<Registrations, UUID> {

    Optional<Registrations> findByDni(String dni);

    // 🔥 NECESARIO PARA QUE EXISTE existsByDni()
    boolean existsByDni(String dni);
}
