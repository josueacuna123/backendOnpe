package com.miapp.backend.model;

import lombok.Data;
import java.util.UUID;

@Data
public class VoteRequest {
    private String dni;
    private UUID candidateId;
    private UUID categoryId;
}
