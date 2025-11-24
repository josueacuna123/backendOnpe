package com.miapp.backend.controller;

import com.miapp.backend.service.VoteUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/votes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ROLE_GENERAL','ROLE_VOTACIONES','ROLE_ADMIN')")
public class VoteUploadController {

    private final VoteUploadService voteUploadService;

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadVotesCsv(@RequestParam("file") MultipartFile file) {
        try {
            String result = voteUploadService.processCsv(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error procesando CSV: " + e.getMessage());
        }
    }
}
