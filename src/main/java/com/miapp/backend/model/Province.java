package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "provinces")
@Data
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String name;
}
