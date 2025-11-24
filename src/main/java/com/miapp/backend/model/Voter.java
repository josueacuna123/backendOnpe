package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "registrations", schema = "public")
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String dni;

    private String full_name;

    // CITY = DEPARTMENT
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    private String photo_path;
    private String status;
    private String reason;

    private Instant created_at;

    @Column(name = "face_embedding", columnDefinition = "TEXT")
    private String face_embedding;
}
