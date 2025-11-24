package com.miapp.backend.util;

import com.miapp.backend.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VotesCsvParser {

    public static List<Vote> parse(MultipartFile file) throws Exception {

        List<Vote> votes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (first) { first = false; continue; } // Omitir encabezado

                String[] parts = line.split(",");

                if (parts.length < 3)
                    throw new RuntimeException("Fila CSV inválida: " + line);

                String voterDni   = parts[0].trim();
                String candidateId = parts[1].trim();
                String categoryId  = parts[2].trim();

                Integer departmentId = parts.length > 3 && !parts[3].isEmpty() ? Integer.parseInt(parts[3]) : null;
                Integer provinceId   = parts.length > 4 && !parts[4].isEmpty() ? Integer.parseInt(parts[4]) : null;
                Integer districtId   = parts.length > 5 && !parts[5].isEmpty() ? Integer.parseInt(parts[5]) : null;

                Vote v = new Vote();
                v.setVoterDni(voterDni);
                v.setCreatedAt(Instant.now());

                // 1. Candidato
                Candidate c = new Candidate();
                c.setId(UUID.fromString(candidateId));
                v.setCandidate(c);

                // 2. Categoría
                ElectionCategory cat = new ElectionCategory();
                cat.setId(UUID.fromString(categoryId));
                v.setCategory(cat);

                // 3. Ubicación (objetos con solo ID)
                if (departmentId != null) {
                    Department d = new Department();
                    d.setId(departmentId);
                    v.setDepartment(d);
                }

                if (provinceId != null) {
                    Province p = new Province();
                    p.setId(provinceId);
                    v.setProvince(p);
                }

                if (districtId != null) {
                    District d = new District();
                    d.setId(districtId);
                    v.setDistrict(d);
                }

                votes.add(v);
            }
        }

        return votes;
    }
}
