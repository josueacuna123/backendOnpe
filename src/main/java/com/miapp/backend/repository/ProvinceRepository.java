package com.miapp.backend.repository;

import com.miapp.backend.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    Optional<Province> findByNameIgnoreCase(String name);

    List<Province> findByDepartmentId(Integer departmentId);  // 👈 ESTE MÉTODO
}
