package com.miapp.backend.controller;

import com.miapp.backend.model.Candidate;
import com.miapp.backend.model.Vote;
import com.miapp.backend.model.VoteRequest;
import com.miapp.backend.service.VoteService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // 🔥 Nuevo endpoint seguro que filtra por DNI automáticamente
    @GetMapping("/candidates/{categoryId}/{dni}")
    public List<Candidate> getCandidates(
            @PathVariable UUID categoryId,
            @PathVariable String dni
    ) {
        return voteService.getCandidatesFiltered(categoryId, dni);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitVote(@RequestBody VoteRequest request) {
        try {
            Vote vote = voteService.registerVote(
                    request.getDni(),
                    request.getCandidateId(),
                    request.getCategoryId()
            );
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
