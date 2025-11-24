package com.miapp.backend.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VoteCountDTO {
    private UUID candidateId;
    private String candidateName;
    private Long voteCount;
}