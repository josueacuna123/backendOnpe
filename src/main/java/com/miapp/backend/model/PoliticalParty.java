package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;  // <-- ESTE IMPORT
import java.util.UUID;

@Entity
@Table(name = "political_parties")
@Data
public class PoliticalParty {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
