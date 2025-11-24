package com.miapp.backend.service;

import com.miapp.backend.model.*;
import com.miapp.backend.repository.*;
import com.miapp.backend.util.VotesCsvParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteUploadService {

    private final VoteRepository voteRepository;
    private final RegistrationsRepository registrationsRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionCategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    public String processCsv(MultipartFile file) throws Exception {

        List<Vote> votes = VotesCsvParser.parse(file);

        int inserted = 0;

        for (Vote vote : votes) {

            // VALIDACIONES -------------------------------

            // 1. DNI existente
            if (!registrationsRepository.existsByDni(vote.getVoterDni())) {
                throw new RuntimeException("El DNI " + vote.getVoterDni() + " no existe en registrations.");
            }

            // 2. Candidato existente
            if (!candidateRepository.existsById(vote.getCandidate().getId())) {
                throw new RuntimeException("candidate_id inválido: " + vote.getCandidate().getId());
            }

            // 3. Categoría existente
            if (!categoryRepository.existsById(vote.getCategory().getId())) {
                throw new RuntimeException("category_id inválido: " + vote.getCategory().getId());
            }

            // 4. Ubicación válida
            if (vote.getDepartment() != null &&
                vote.getDepartment().getId() != null &&
                !departmentRepository.existsById(vote.getDepartment().getId())) {

                throw new RuntimeException("department_id inválido: " + vote.getDepartment().getId());
            }

            if (vote.getProvince() != null &&
                vote.getProvince().getId() != null &&
                !provinceRepository.existsById(vote.getProvince().getId())) {

                throw new RuntimeException("province_id inválido: " + vote.getProvince().getId());
            }

            if (vote.getDistrict() != null &&
                vote.getDistrict().getId() != null &&
                !districtRepository.existsById(vote.getDistrict().getId())) {

                throw new RuntimeException("district_id inválido: " + vote.getDistrict().getId());
            }


            // 5. Restricción única: voter_dni + category_id
            if (voteRepository.existsByVoterDniAndCategoryId(
                    vote.getVoterDni(), vote.getCategory().getId())) {
                throw new RuntimeException(
                        "El votante " + vote.getVoterDni() +
                        " ya tiene un voto en la categoría " + vote.getCategory().getId()
                );
            }

            // INSERTAR -----------------------------------
            voteRepository.save(vote);
            inserted++;
        }

        return "CSV procesado correctamente. Votos insertados: " + inserted;
    }
}
