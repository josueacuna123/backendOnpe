package com.miapp.backend.repository;

import com.miapp.backend.model.Vote;
import com.miapp.backend.dto.VoteCountDTO; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List; // <-- ¡Necesitas esta importación para Optional!
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    
    /**
     * CORRECCIÓN: Utiliza JOIN FETCH para cargar Candidate, Party y Category (EAGERLY).
     * Esto resuelve el problema de "N/A" causado por Lazy Loading en RegistrationService.
     * Devuelve una lista para soportar múltiples votos de un DNI (uno por categoría).
     */
// Archivo: VoteRepository.java

    @Query("SELECT v FROM Vote v LEFT JOIN FETCH v.candidate c LEFT JOIN FETCH c.party LEFT JOIN FETCH v.category WHERE v.voterDni = :voterDni")
    List<Vote> findByVoterDni(@Param("voterDni") String voterDni);

    // Método ya existente para verificar si ya votó
    boolean existsByVoterDniAndCategoryId(String dni, UUID categoryId);

    // 🔥 CONSULTA JPQL PARA CONTEO DINÁMICO (EXISTENTE) 🔥
    @Query("SELECT NEW com.miapp.backend.dto.VoteCountDTO(c.id, c.fullName, COUNT(v.id)) " +
           "FROM Vote v JOIN v.candidate c " +
           "WHERE v.category.id = :categoryId " +
           // Filtro Condicional por Departamento (si departmentId no es null, aplica el filtro)
           "AND (CAST(:departmentId AS integer) IS NULL OR v.department.id = :departmentId) " +
           // Filtro Condicional por Provincia
           "AND (CAST(:provinceId AS integer) IS NULL OR v.province.id = :provinceId) " +
           // Filtro Condicional por Distrito
           "AND (CAST(:districtId AS integer) IS NULL OR v.district.id = :districtId) " +
           "GROUP BY c.id, c.fullName ORDER BY COUNT(v.id) DESC")
    List<VoteCountDTO> countVotesByCandidateAndLocation(
        @Param("categoryId") UUID categoryId,
        @Param("departmentId") Integer departmentId,
        @Param("provinceId") Integer provinceId,
        @Param("districtId") Integer districtId
    );
}