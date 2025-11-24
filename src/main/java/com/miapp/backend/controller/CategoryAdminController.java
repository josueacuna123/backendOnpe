package com.miapp.backend.controller;

import com.miapp.backend.model.ElectionCategory;
import com.miapp.backend.repository.ElectionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// 🔥 Mapeado a la URL PROTEGIDA que el frontend de candidatos está llamando
@RequestMapping("/api/public/categories")
@CrossOrigin(origins = "*") 
public class CategoryAdminController {

    @Autowired
    private ElectionCategoryRepository categoryRepo; // Usa el mismo repositorio

    @GetMapping
    public List<ElectionCategory> getCategoriesForAdmin() {
        return categoryRepo.findAll();
    }
}