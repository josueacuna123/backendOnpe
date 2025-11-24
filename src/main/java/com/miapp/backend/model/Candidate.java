package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "candidates")
@Data
public class Candidate {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private PoliticalParty party;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ElectionCategory category;

    // 🔹 Ubicación normalizada
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;   // puede ser null (nacional / distrital, etc)

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;       // puede ser null

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;       // puede ser null

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
