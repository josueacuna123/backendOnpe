package com.miapp.backend.service;

import com.miapp.backend.model.Candidate;
import com.miapp.backend.dto.CreateCandidateRequest;
import com.miapp.backend.dto.UpdateCandidateRequest;
import com.miapp.backend.repository.CandidateRepository;
import com.miapp.backend.repository.PoliticalPartyRepository;
import com.miapp.backend.repository.ElectionCategoryRepository;
import com.miapp.backend.repository.DepartmentRepository;
import com.miapp.backend.repository.ProvinceRepository;
import com.miapp.backend.repository.DistrictRepository; // Asegúrate de tener este import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private PoliticalPartyRepository partyRepository;
    @Autowired
    private ElectionCategoryRepository categoryRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;

    // --- MÉTODOS HELPER (Para manejar diferentes tipos de claves primarias) ---

    // 🔥 Método Helper 1: Para entidades con clave primaria UUID (Candidate, Party, Category)
    private <T> T findEntityByUUIDId(UUID id, JpaRepository<T, UUID> repository, String entityName) {
        if (id == null) return null;
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(entityName + " con ID " + id + " no encontrado."));
    }

    // 🔥 Método Helper 2: Para entidades con clave primaria Integer (Department, Province, District)
    private <T> T findEntityByIntegerId(Integer id, JpaRepository<T, Integer> repository, String entityName) {
        if (id == null) return null;
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(entityName + " con ID " + id + " no encontrado."));
    }

    // --- LÓGICA CRUD ---

    /**
     * Crea un nuevo candidato en el sistema.
     * Mapea los IDs de los DTO a las entidades JPA.
     */
    public Candidate createCandidate(CreateCandidateRequest req) {
        Candidate candidate = new Candidate();
        
        candidate.setFullName(req.getFullName());
        candidate.setPhotoPath(req.getPhotoPath());

        // Mapeo de IDs de UUID
        candidate.setParty(findEntityByUUIDId(req.getPartyId(), partyRepository, "Partido"));
        candidate.setCategory(findEntityByUUIDId(req.getCategoryId(), categoryRepository, "Categoría"));

        // Mapeo de IDs de Integer (Ubicación)
        candidate.setDepartment(findEntityByIntegerId(req.getDepartmentId(), departmentRepository, "Departamento"));
        candidate.setProvince(findEntityByIntegerId(req.getProvinceId(), provinceRepository, "Provincia"));
        candidate.setDistrict(findEntityByIntegerId(req.getDistrictId(), districtRepository, "Distrito"));

        return candidateRepository.save(candidate);
    }

    /**
     * Obtiene la lista de candidatos, aplicando un filtro de búsqueda por nombre si se proporciona.
     */
    public List<Candidate> getCandidatesByFilter(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return candidateRepository.findAll();
        }
        // Asumo que tienes un método 'findByFullNameContainingIgnoreCase' en CandidateRepository
        // Si no existe, deberás agregarlo o usar una @Query de JPA.
        return candidateRepository.findByFullNameContainingIgnoreCase(searchTerm); 
    }
    
    /**
     * Actualiza la información de un candidato existente.
     */
    public Candidate updateCandidate(UUID id, UpdateCandidateRequest req) {
        Candidate candidate = candidateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Candidato con ID " + id + " no encontrado para actualizar."));

        candidate.setFullName(req.getFullName());
        candidate.setPhotoPath(req.getPhotoPath());

        // Actualizar relaciones (Mapeo de IDs de UUID)
        candidate.setParty(findEntityByUUIDId(req.getPartyId(), partyRepository, "Partido"));
        candidate.setCategory(findEntityByUUIDId(req.getCategoryId(), categoryRepository, "Categoría"));
        
        // Actualizar Ubicación (Mapeo de IDs de Integer)
        candidate.setDepartment(findEntityByIntegerId(req.getDepartmentId(), departmentRepository, "Departamento"));
        candidate.setProvince(findEntityByIntegerId(req.getProvinceId(), provinceRepository, "Provincia"));
        candidate.setDistrict(findEntityByIntegerId(req.getDistrictId(), districtRepository, "Distrito"));

        return candidateRepository.save(candidate);
    }

    /**
     * Elimina un candidato por su ID.
     */
    public void deleteCandidate(UUID id) {
        if (!candidateRepository.existsById(id)) {
             throw new EntityNotFoundException("Candidato con ID " + id + " no encontrado para eliminar.");
        }
        candidateRepository.deleteById(id);
    }
}