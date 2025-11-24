package com.miapp.backend.controller;

import com.miapp.backend.model.Registrations;
import com.miapp.backend.model.ExtraDataRequest;

import com.miapp.backend.model.RegistrationAttempts;
import com.miapp.backend.repository.RegistrationsRepository;

import com.miapp.backend.repository.RegistrationAttemptsRepository;
import com.miapp.backend.service.FaceService;
import com.miapp.backend.service.DniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationsRepository registrationsRepo;
    private final RegistrationAttemptsRepository attemptsRepo;
    private final FaceService faceService;
    private final DniService dniService;


    // UMBRALES DE SEGURIDAD (IGUAL QUE ANTES)
    private static final double HARD_THRESHOLD = 0.72;
    private static final double SOFT_THRESHOLD = 0.60;

    // ============================================================
    //  ENTRYPOINT PRINCIPAL: AUTH
    // ============================================================
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(
            @RequestParam("dni") String dni,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "departamento", required = false) String departamento,
            @RequestParam(value = "provincia", required = false) String provincia,
            @RequestParam(value = "distrito", required = false) String distrito
    ) {
        Optional<Registrations> existingOpt = registrationsRepo.findByDni(dni);

        if (existingOpt.isEmpty()) {
            // Usuario primerizo
            return registerFlow(dni, file, departamento, provincia, distrito);
        } else {
            // Usuario existente, solo login facial
            return loginFlow(existingOpt.get(), file);
        }
    }

    // ============================================================
    //  REGISTRO (USUARIO NUEVO)
    // ============================================================
    private ResponseEntity<?> registerFlow(
            String dni,
            MultipartFile imageFile,
            String departamento,
            String provincia,
            String distrito
    ) {
        try {
            // 1) Consultamos API DNI (igual que antes)
            Map<String, Object> dniResponse = dniService.getDniData(dni);
            boolean success = (dniResponse.get("success") instanceof Boolean b && b);

            if (!success) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "DNI no válido"
                ));
            }

            Map<String, Object> apiData = (Map<String, Object>) dniResponse.get("data");

            String fullName = String.valueOf(apiData.getOrDefault("nombre_completo", ""));
            String apiDept = String.valueOf(apiData.getOrDefault("departamento", ""));
            String apiProv = String.valueOf(apiData.getOrDefault("provincia", ""));
            String apiDist = String.valueOf(apiData.getOrDefault("distrito", ""));

            // 1) Preferimos lo manual si viene
            String finalDept = (departamento != null && !departamento.isBlank()) ? departamento : apiDept;
            String finalProv = (provincia != null && !provincia.isBlank()) ? provincia : apiProv;
            String finalDist = (distrito != null && !distrito.isBlank()) ? distrito : apiDist;

            // 2) Convertir cadenas en ENTIDADES reales

            Integer depId = Integer.parseInt(finalDept);
            Integer provId = Integer.parseInt(finalProv);
            Integer distId = Integer.parseInt(finalDist);


            // 3) Extraer embedding facial (igual que antes)
            float[] embedding = faceService.getEmbeddingFromPython(imageFile);

            // 4) Comparación facial (igual que antes)
            List<Registrations> all = registrationsRepo.findAll();
            FaceService.MatchResult match = faceService.findBestMatch(embedding, all);
            double sim = match.similarity();

            // 5) Procesos de fraude — sin cambios
            RegistrationAttempts attempt = new RegistrationAttempts();
            attempt.setDni(dni);
            attempt.setSimilarity((float) sim);
            attempt.setCreated_at(Instant.now());

            if (match.registration() != null) {
                attempt.setCandidate_registration_id(match.registration().getId());
            }

            // --- REGLAS IGUALES ---
            if (match.registration() != null &&
                    !match.registration().getDni().equals(dni) &&
                    sim > 0.60) {

                attempt.setVerdict("rejected");
                attempt.setReason("fraud_same_face_different_dni");
                attemptsRepo.save(attempt);

                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Este rostro ya está registrado con otro DNI"
                ));
            }

            if (sim > HARD_THRESHOLD) {
                attempt.setVerdict("rejected");
                attempt.setReason("fraud");
                attemptsRepo.save(attempt);

                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Posible fraude facial"
                ));
            }

            if (sim >= SOFT_THRESHOLD) {
                attempt.setVerdict("flagged");
                attempt.setReason("suspect");
                attemptsRepo.save(attempt);

                return ResponseEntity.status(409).body(Map.of(
                        "status", "flagged",
                        "message", "Registro en revisión manual"
                ));
            }

            // 6) Registro OK
            attempt.setVerdict("accepted");
            attemptsRepo.save(attempt);

            Registrations reg = new Registrations();
                reg.setDni(dni);
                reg.setFull_name(fullName);

                reg.setDepartment_id(depId);
                reg.setProvince_id(provId);
                reg.setDistrict_id(distId);

                // Guardar foto
                String photoPath = savePhoto(imageFile, dni);
                reg.setPhoto_path(photoPath);

                reg.setStatus("registered");
                reg.setCreated_at(Instant.now());
                reg.setFace_embedding(faceService.embeddingToString(embedding));

                registrationsRepo.save(reg);

                return ResponseEntity.ok(Map.of(
                        "status", "registered",
                        "message", "Usuario registrado correctamente",
                        "dni", dni,
                        "nombre", fullName,
                        "departamento", finalDept,
                        "provincia", finalProv,
                        "distrito", finalDist,
                        "photo_path", photoPath  // 🔥 AÑADIDO
                ));


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error en registro",
                    "detail", e.getMessage()
            ));
        }
    }
    // === Guardar foto en carpeta local y devolver ruta accesible ===
    private String savePhoto(MultipartFile file, String dni) throws Exception {
        String folder = "src/main/resources/static/photos/";

        // Crear carpeta si no existe
        java.nio.file.Files.createDirectories(
                java.nio.file.Paths.get(folder)
        );

        // Nombre único
        String fileName = dni + "_" + System.currentTimeMillis() + ".jpg";

        java.nio.file.Path path = java.nio.file.Paths.get(folder + fileName);

        java.nio.file.Files.write(path, file.getBytes());

        // Será accesible desde http://localhost:8080/photos/archivo.jpg
        return "photos/" + fileName;
    }



    // ============================================================
    //  LOGIN USUARIO EXISTENTE
    // ============================================================
    private ResponseEntity<?> loginFlow(Registrations reg, MultipartFile file) {

        try {
            float[] candidate = faceService.getEmbeddingFromPython(file);

            if (reg.getFace_embedding() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Usuario sin embedding registrado"
                ));
            }

            float[] stored = faceService.stringToEmbedding(reg.getFace_embedding());
            double similarity = faceService.cosineSimilarity(candidate, stored);

            if (similarity < 0.55) {
                return ResponseEntity.status(401).body(Map.of(
                        "status", "error",
                        "message", "La verificación facial falló",
                        "similarity", similarity
                ));
            }

            return ResponseEntity.ok(Map.of(
                "status", "login_ok",
                "message", "Login exitoso",
                "user", reg.getFull_name(),
                "similarity", similarity,
                "dni", reg.getDni(),

                // 🔹 AGREGAR ESTOS:
                "departamento", reg.getDepartment_id(),
                "provincia", reg.getProvince_id(),
                "distrito", reg.getDistrict_id(),
                "photo_path", reg.getPhoto_path()
                
        ));


        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Error en login",
                    "detail", e.getMessage()
            ));
        }
    }

    // ============================================================
    //  CONSULTA API DNI
    // ============================================================

    // ============================================================
    //  OPCIONAL: ENDPOINT PARA ACTUALIZAR DATOS EXTRA
    //  (ya no lo usamos en el flujo normal, pero lo dejamos por si acaso)
    // ============================================================
    @PostMapping("/save-extra-data")
    public ResponseEntity<?> saveExtraData(@RequestBody ExtraDataRequest req) {

        Optional<Registrations> optional = registrationsRepo.findByDni(req.getDni());

        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "El usuario aún no está registrado"
                    )
            );
        }

        Registrations reg = optional.get();

        try {

                        // Buscar entidades por ID (el frontend envía enteros)
        reg.setDepartment_id(req.getDepartmentId());
        reg.setProvince_id(req.getProvinceId());
        reg.setDistrict_id(req.getDistrictId());

        registrationsRepo.save(reg);


            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Datos actualizados correctamente"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Error al actualizar datos",
                            "detail", e.getMessage()
                    )
            );
        }
    }



}
