package com.miapp.backend.controller;

import com.miapp.backend.model.ElectionCategory;
import com.miapp.backend.repository.ElectionCategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "*")
public class CategoryLockController {

    private final ElectionCategoryRepository repo;

    public CategoryLockController(ElectionCategoryRepository repo) {
        this.repo = repo;
    }

    // 🔒 BLOQUEAR / DESBLOQUEAR CATEGORÍA
    @PatchMapping("/{id}/lock")
    public ElectionCategory toggleLock(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean locked = body.get("locked");

        ElectionCategory category = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setLocked(locked);

        return repo.save(category);
    }
}
