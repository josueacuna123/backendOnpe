package com.miapp.backend.service;

import com.miapp.backend.dto.RegistrationListDTO;
import com.miapp.backend.dto.VoterRegistrationDTO;
import com.miapp.backend.model.Registrations;
import com.miapp.backend.model.Vote;
import com.miapp.backend.model.Candidate;

import com.miapp.backend.repository.RegistrationsRepository;
import com.miapp.backend.repository.VoteRepository;
import com.miapp.backend.repository.DepartmentRepository;
import com.miapp.backend.repository.ProvinceRepository;
import com.miapp.backend.repository.DistrictRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationsRepository registrationsRepository;
    private final VoteRepository voteRepository;
    private final DepartmentRepository departmentRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;


    // ========================================================================
    // 1. LISTAR VOTOS (SIN FILTROS)
    // ========================================================================
    public List<VoterRegistrationDTO> findAllVoterRegistrations() {
        return findAllVoterRegistrationsWithFilters(
                null, null, null,
                null, null, null
        );
    }


    // ========================================================================
    // 2. LISTAR VOTOS (CON FILTROS)
    // ========================================================================
    public List<VoterRegistrationDTO> findAllVoterRegistrationsWithFilters(
            String dniFilter,
            String nameFilter,
            String categoryNameFilter,
            Integer departmentIdFilter,
            Integer provinceIdFilter,
            Integer districtIdFilter) {

        List<Vote> allVotes = voteRepository.findAll();
        Map<String, Registrations> registrationCache = new HashMap<>();

        return allVotes.stream()

                // FILTRO DNI
                .filter(v ->
                        dniFilter == null ||
                        dniFilter.isEmpty() ||
                        v.getVoterDni().contains(dniFilter)
                )

                // FILTRO CATEGORÍA
                .filter(v ->
                        categoryNameFilter == null ||
                        categoryNameFilter.isEmpty() ||
                        (v.getCategory() != null &&
                         v.getCategory().getName().equalsIgnoreCase(categoryNameFilter))
                )

                // MAP A DTO
                .map(vote -> {

                    Registrations reg = registrationCache.computeIfAbsent(
                            vote.getVoterDni(),
                            dni -> registrationsRepository.findByDni(dni).orElse(null)
                    );

                    VoterRegistrationDTO dto = mapVoteAndRegistrationToDTO(vote, reg);

                    if (reg == null) {
                        if (nameFilter != null || departmentIdFilter != null ||
                            provinceIdFilter != null || districtIdFilter != null) {
                            return null;
                        }
                        return dto;
                    }

                    // FILTRO NOMBRE
                    if (nameFilter != null && !nameFilter.isEmpty()) {
                        if (reg.getFull_name() == null ||
                            !reg.getFull_name().toLowerCase().contains(nameFilter.toLowerCase())) {
                            return null;
                        }
                    }

                    // FILTRO UBIGEO
                    if (departmentIdFilter != null &&
                        !departmentIdFilter.equals(reg.getDepartment_id())) return null;

                    if (provinceIdFilter != null &&
                        !provinceIdFilter.equals(reg.getProvince_id())) return null;

                    if (districtIdFilter != null &&
                        !districtIdFilter.equals(reg.getDistrict_id())) return null;

                    return dto;
                })

                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }




    // ========================================================================
    // 3. LISTAR REGISTROS (PANEL ADMIN)
    // ========================================================================
    public List<RegistrationListDTO> listRegistrations() {

        return registrationsRepository.findAll().stream().map(r -> {
            RegistrationListDTO dto = new RegistrationListDTO();

            dto.setId(r.getId());
            dto.setDni(r.getDni());
            dto.setFull_name(r.getFull_name());

            // Convertir IDs → nombres
            dto.setDepartment(
                    departmentRepository.findById(r.getDepartment_id())
                            .map(dep -> dep.getName())
                            .orElse("N/A")
            );

            dto.setProvince(
                    provinceRepository.findById(r.getProvince_id())
                            .map(p -> p.getName())
                            .orElse("N/A")
            );

            dto.setDistrict(
                    districtRepository.findById(r.getDistrict_id())
                            .map(d -> d.getName())
                            .orElse("N/A")
            );

            dto.setPhoto_path(r.getPhoto_path());

            return dto;

        }).collect(Collectors.toList());
    }





    // ========================================================================
    // 4. MÉTODO QUE USA EL CONTROLADOR PARA ELIMINAR
    // ========================================================================
    public boolean exists(UUID id) {
        return registrationsRepository.existsById(id);
    }

    public void delete(UUID id) {
        registrationsRepository.deleteById(id);
    }




    // ========================================================================
    // 5. MAPEO A DTO (VOTOS)
    // ========================================================================
    private VoterRegistrationDTO mapVoteAndRegistrationToDTO(
            Vote vote, Registrations registrationEntity) {

        VoterRegistrationDTO dto = new VoterRegistrationDTO();

        dto.setVoteId(vote.getId().toString());
        dto.setVoterDNI(vote.getVoterDni());
        dto.setVoteStatus("VOTÓ");

        Candidate candidate = vote.getCandidate();

        if (candidate != null) {
            dto.setCandidateFullName(candidate.getFullName());
            dto.setPartyName(
                    candidate.getParty() != null ?
                            candidate.getParty().getName() : "N/A"
            );
            dto.setCategoryName(
                    vote.getCategory() != null ?
                            vote.getCategory().getName() : "N/A"
            );

        } else {
            dto.setCandidateFullName("N/A");
            dto.setPartyName("N/A");
            dto.setCategoryName("N/A");
        }

        if (registrationEntity != null) {

            dto.setRegistrationId(registrationEntity.getId().toString());
            dto.setVoterName(
                    registrationEntity.getFull_name() != null ?
                            registrationEntity.getFull_name() : "N/A"
            );

            if (registrationEntity.getCreated_at() != null) {
                LocalDateTime localDateTime =
                        registrationEntity.getCreated_at()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                dto.setRegistrationDate(localDateTime);
            }

            // UBIGEO
            departmentRepository.findById(registrationEntity.getDepartment_id())
                    .ifPresent(d -> dto.setDepartmentName(d.getName()));

            provinceRepository.findById(registrationEntity.getProvince_id())
                    .ifPresent(p -> dto.setProvinceName(p.getName()));

            districtRepository.findById(registrationEntity.getDistrict_id())
                    .ifPresent(d -> dto.setDistrictName(d.getName()));

        } else {
            dto.setRegistrationId("N/A");
            dto.setVoterName("N/A");
            dto.setDepartmentName("N/A");
            dto.setProvinceName("N/A");
            dto.setDistrictName("N/A");
        }

        return dto;
    }

}
