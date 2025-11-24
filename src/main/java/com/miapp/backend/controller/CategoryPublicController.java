package com.miapp.backend.controller;

import com.miapp.backend.model.ElectionCategory;
import com.miapp.backend.repository.ElectionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories") 
@CrossOrigin(origins = "*") 
public class CategoryPublicController {

    @Autowired
    private ElectionCategoryRepository categoryRepo;

    @GetMapping 
    public List<ElectionCategory> getCategoriesForPublic() {
        return categoryRepo.findAll()
        .stream()
        .filter(cat -> !cat.isLocked())  // solo desbloqueadas
        .toList();
    }
}
