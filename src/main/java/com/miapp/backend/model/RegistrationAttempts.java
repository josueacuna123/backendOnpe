package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "registration_attempts", schema = "public")
public class RegistrationAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String dni;

    // similaridad con el mejor candidato (cosine)
    private Float similarity;

    // accepted | rejected | flagged
    private String verdict;

    // motivo (fraud, suspect, etc.)
    private String reason;

    @Column(columnDefinition = "uuid")
    private UUID candidate_registration_id;
    
    private Instant created_at;
}
