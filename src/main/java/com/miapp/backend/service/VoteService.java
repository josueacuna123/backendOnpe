package com.miapp.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.miapp.backend.model.Vote;
import com.miapp.backend.dto.VoteCountDTO;
import com.miapp.backend.model.Candidate;
import com.miapp.backend.model.ElectionCategory;
import com.miapp.backend.model.Registrations;
import com.miapp.backend.repository.VoteRepository;
import com.miapp.backend.repository.CandidateRepository;
import com.miapp.backend.repository.DepartmentRepository;
import com.miapp.backend.repository.DistrictRepository;
import com.miapp.backend.repository.ElectionCategoryRepository;
import com.miapp.backend.repository.ProvinceRepository;
import com.miapp.backend.repository.RegistrationsRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionCategoryRepository categoryRepository;
    private final RegistrationsRepository registrationsRepository;

    // ==========================================================
    //   🔹 Registrar Voto
    // ==========================================================
    // VoteService.java (AGREGAR ESTAS INYECCIONES)
// Asegúrate de inyectar los repositorios de Ubicación
private final DepartmentRepository departmentRepository; 
private final ProvinceRepository provinceRepository;
private final DistrictRepository districtRepository;

// (Nota: Necesitas hacer un @Autowired o pasarlos en el constructor si usas @RequiredArgsConstructor)

// ==========================================================
//   🔹 Registrar Voto (CORREGIDO)
// ==========================================================
    public Vote registerVote(String dni, UUID candidateId, UUID categoryId) throws Exception {

        if (voteRepository.existsByVoterDniAndCategoryId(dni, categoryId)) {
            throw new Exception("Ya has votado en esta categoría");
        }

        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new Exception("Candidato no encontrado"));

        ElectionCategory category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new Exception("Categoría no encontrada"));

        // 🔥 1. OBTENER UBICACIÓN DEL VOTANTE (Desde Registrations) 🔥
        Registrations voter = registrationsRepository.findByDni(dni)
            .orElseThrow(() -> new Exception("Votante no encontrado en el registro"));

        // 🔥 2. CREAR Y ASIGNAR EL VOTO 🔥
        Vote vote = new Vote();
        vote.setVoterDni(dni);
        vote.setCandidate(candidate);
        vote.setCategory(category);

        // 🔥 3. ASIGNAR LAS ENTIDADES DE UBICACIÓN AL VOTO 🔥
        // Usamos los IDs del Registrations del votante para buscar las entidades
        vote.setDepartment(departmentRepository.findById(voter.getDepartment_id()).orElse(null));
        vote.setProvince(provinceRepository.findById(voter.getProvince_id()).orElse(null));
        vote.setDistrict(districtRepository.findById(voter.getDistrict_id()).orElse(null));
        // Nota: Usamos orElse(null) si la ubicación no es obligatoria para todos los niveles

        return voteRepository.save(vote);
    }
// ... (El resto de tus métodos son correctos)

    // ==========================================================
    //   🔹 Obtener candidatos filtrados por ubicación del votante
    // ==========================================================
    // VoteService.java (Método getCandidatesFiltered - CORREGIDO)

    public List<Candidate> getCandidatesFiltered(UUID categoryId, String dni) {

        // 1. Obtener los IDs de Ubicación del Votante (Registrations)
        // Usamos Registrations para obtener la ubicación del votante
        Registrations voter = registrationsRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Votante no encontrado"));

        // Guardamos los IDs de ubicación del votante
        final Integer voterDepId = voter.getDepartment_id(); 
        final Integer voterProvId = voter.getProvince_id();
        final Integer voterDistId = voter.getDistrict_id();

        // 2. Obtener la Categoría de Elección y su Nivel
        ElectionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        // Obtenemos el nivel de la CATEGORÍA seleccionada (Ej: "REGIONAL", "PROVINCIAL")
        String level = category.getLevel().toUpperCase();

        // 3. Aplicar filtro basado en el NIVEL de la CATEGORÍA
        switch (level) {

            case "REGIONAL":
                // Si la categoría es Regional, filtramos por el DEPARTAMENTO del votante.
                // (Ej: Candidatos a Gobernador Regional, solo los de depId 15)
                if (voterDepId == null) return List.of(); // Sanity check
                return candidateRepository.findByCategoryAndDepartment(categoryId, voterDepId); 

            case "PROVINCIAL":
                // Si la categoría es Provincial, filtramos por la PROVINCIA del votante.
                // (Ej: Candidatos a Alcalde Provincial, solo los de provId 1501)
                if (voterProvId == null) return List.of(); // Sanity check
                return candidateRepository.findByCategoryAndProvince(categoryId, voterProvId); 

            case "DISTRITAL":
                // Si la categoría es Distrital, filtramos por el DISTRITO del votante.
                // (Ej: Candidatos a Alcalde Distrital, solo los de distId 150110)
                if (voterDistId == null) return List.of(); // Sanity check
                return candidateRepository.findByCategoryAndDistrict(categoryId, voterDistId); 

            case "NACIONAL":
            default:
                // Si es Nacional (Presidente/Congreso), no hay filtro geográfico.
                return candidateRepository.findByCategoryId(categoryId);
        }
    }
    // En VoteService.java, después de los otros métodos CRUD/filtrado...

// ==========================================================
//   🔹 Reporte de Conteo de Votos (Estadísticas Admin)
// ==========================================================
    public List<VoteCountDTO> getVoteCounts(
        UUID categoryId,
        Integer departmentId,
        Integer provinceId,
        Integer districtId
    ) {
        if (categoryId == null) {
            throw new IllegalArgumentException("El ID de Categoría es obligatorio para el conteo de votos.");
        }

        // Llama a la consulta JPQL dinámica
        return voteRepository.countVotesByCandidateAndLocation(
            categoryId,
            departmentId,
            provinceId,
            districtId
        );
    }
}