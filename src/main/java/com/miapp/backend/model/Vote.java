package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "votes")
@Data
public class Vote {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "voter_dni", nullable = false)
    private String voterDni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ElectionCategory category;

    // 🔥 CAMPOS AGREGADOS PARA FILTRADO GEOGRÁFICO 🔥

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") // Clave foránea del Departamento
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id") // Clave foránea de la Provincia
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id") // Clave foránea del Distrito
    private District district;
    
    // 🔥 FIN DE CAMPOS AGREGADOS 🔥

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}