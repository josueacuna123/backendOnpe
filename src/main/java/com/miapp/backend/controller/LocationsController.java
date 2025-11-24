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
// 🛠️ MODIFICACIÓN: Se añade /v1 para que la ruta coincida con el frontend
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationsController {

    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    @GetMapping("/provinces")
    public List<Province> getProvinces(@RequestParam Integer departmentId) {
        return provinceRepository.findByDepartmentId(departmentId);
    }

    @GetMapping("/districts")
    public List<District> getDistricts(@RequestParam Integer provinceId) {
        return districtRepository.findByProvinceId(provinceId);
    }
}
