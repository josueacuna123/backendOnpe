/*package com.miapp.backend.controller;

import com.miapp.backend.model.ElectionCategory;
import com.miapp.backend.repository.ElectionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")   // permite al frontend llamar a este endpoint
public class CategoryController {

    @Autowired
    private ElectionCategoryRepository categoryRepo;

    @GetMapping
    public List<ElectionCategory> getCategories() {
        return categoryRepo.findAll();
    }
}*/
