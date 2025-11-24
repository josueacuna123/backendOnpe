package com.miapp.backend.repository;

import com.miapp.backend.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;   // 👈 AGREGA ESTO
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Integer> {

    Optional<District> findByNameIgnoreCase(String name);

    List<District> findByProvinceId(Integer provinceId); // ✔ ahora funciona
}
