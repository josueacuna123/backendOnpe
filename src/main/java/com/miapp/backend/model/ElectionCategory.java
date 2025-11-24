package com.miapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@Entity
@Table(name = "election_categories")

public class ElectionCategory {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    

    @Column(name = "level")
    private String level;  // ⭐ AHORA EXISTE GETLEVEL()

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(nullable = false)
    private boolean locked = false;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}

