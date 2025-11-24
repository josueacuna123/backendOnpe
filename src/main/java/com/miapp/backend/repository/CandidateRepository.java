package com.miapp.backend.repository;

import com.miapp.backend.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    List<Candidate> findByCategoryId(UUID categoryId);
    List<Candidate> findByFullNameContainingIgnoreCase(String fullName);

    // **********************************************
    // 🔥 VERIFICAR ESTRICTAMENTE LA TIPOGRAFÍA
    // **********************************************
    @Query("SELECT c FROM Candidate c WHERE c.category.id = :categoryId AND c.department.id = :departmentId")
    List<Candidate> findByCategoryAndDepartment(@Param("categoryId") UUID categoryId, @Param("departmentId") Integer departmentId);

    @Query("SELECT c FROM Candidate c WHERE c.category.id = :categoryId AND c.province.id = :provinceId")
    List<Candidate> findByCategoryAndProvince(@Param("categoryId") UUID categoryId, @Param("provinceId") Integer provinceId);

    @Query("SELECT c FROM Candidate c WHERE c.category.id = :categoryId AND c.district.id = :districtId")
    List<Candidate> findByCategoryAndDistrict(@Param("categoryId") UUID categoryId, @Param("districtId") Integer districtId);
}