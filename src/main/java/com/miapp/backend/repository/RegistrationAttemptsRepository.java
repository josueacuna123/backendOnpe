package com.miapp.backend.repository;

import com.miapp.backend.model.RegistrationAttempts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistrationAttemptsRepository extends JpaRepository<RegistrationAttempts, UUID> {
}
