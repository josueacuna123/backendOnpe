package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "registrations")
public class Registrations {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String dni;

    private String full_name;

    // 🔥 NUEVO: relaciones reales, NO texto
    @Column(name = "department_id")
    private Integer department_id;

    @Column(name = "province_id")
    private Integer province_id;

    @Column(name = "district_id")
    private Integer district_id;

    private String photo_path;
    private String status;
    private String reason;

    private Instant created_at;

    @Column(name = "face_embedding", columnDefinition = "TEXT")
    private String face_embedding;

    
}
