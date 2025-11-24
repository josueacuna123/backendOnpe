// Archivo: MetadataController.java

package com.miapp.backend.controller;

import com.miapp.backend.model.ElectionCategory; // Usa tu clase de modelo de categoría
import com.miapp.backend.model.Department;
import com.miapp.backend.model.Province; // Necesitas los models de ubicación
import com.miapp.backend.model.District; // Necesitas los models de ubicación

import com.miapp.backend.repository.ElectionCategoryRepository; // Usa tu repositorio de categoría
import com.miapp.backend.repository.DepartmentRepository;
import com.miapp.backend.repository.ProvinceRepository;
import com.miapp.backend.repository.DistrictRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import para seguridad
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_VOTACIONES') or hasRole('ROLE_GENERAL')") // ⬅️ ¡SEGURIDAD ESENCIAL!
public class MetadataController {
    
    // Inyección de Repositorios
    private final ElectionCategoryRepository electionCategoryRepository; 
    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    /**
     * Obtiene todas las categorías de elección para el filtro.
     * Ruta: /api/admin/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<ElectionCategory>> getAllCategories() {
        return ResponseEntity.ok(electionCategoryRepository.findAll());
    }

    /**
     * Obtiene todos los departamentos para el filtro.
     * Ruta: /api/admin/departments
     */
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }
    
    /**
     * Obtiene provincias, opcionalmente filtradas por ID de departamento.
     * Ruta: /api/admin/provinces?departmentId=X
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getProvincesByDepartment(
        @RequestParam(required = false) Integer departmentId) {

        if (departmentId != null) {
            return ResponseEntity.ok(provinceRepository.findByDepartmentId(departmentId));
        }

        return ResponseEntity.ok(provinceRepository.findAll());
    }

    @GetMapping("/districts")
    public ResponseEntity<List<District>> getDistrictsByProvince(
        @RequestParam(required = false) Integer provinceId) {

        if (provinceId != null) {
            return ResponseEntity.ok(districtRepository.findByProvinceId(provinceId));
        }

        return ResponseEntity.ok(districtRepository.findAll());
    }
}