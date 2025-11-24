package com.miapp.backend.controller;

import com.miapp.backend.model.Department;
import com.miapp.backend.model.Province;
import com.miapp.backend.model.District;
import com.miapp.backend.repository.DepartmentRepository;
import com.miapp.backend.repository.ProvinceRepository;
import com.miapp.backend.repository.DistrictRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// 🔥 Mapeado al prefijo /api/public/ que está PROTEGIDO en SecurityConfig
@RequestMapping("/api/public") 
@RequiredArgsConstructor
public class LocationAdminController {

    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @GetMapping("/departments")
    public List<Department> getDepartmentsForAdmin() {
        return departmentRepository.findAll();
    }

    @GetMapping("/provinces")
    public List<Province> getProvincesForAdmin(@RequestParam Integer departmentId) {
        return provinceRepository.findByDepartmentId(departmentId);
    }

    @GetMapping("/districts")
    public List<District> getDistrictsForAdmin(@RequestParam Integer provinceId) {
        return districtRepository.findByProvinceId(provinceId);
    }
}