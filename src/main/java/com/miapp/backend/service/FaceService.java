package com.miapp.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miapp.backend.model.Registrations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class FaceService {

    @Value("${face.service.url:http://localhost:5001/extract}")
    private String faceServiceUrl;

    // NO usamos Lombok aquí para evitar problemas con inyección
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    

    // 1. Llamar al microservicio de Python y obtener el embedding
    public float[] getEmbeddingFromPython(MultipartFile imageFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource imageResource = new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return imageFile.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", imageResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                faceServiceUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Error al llamar al microservicio de rostro");
        }

        Object rawEmbedding = response.getBody().get("embedding");
        if (!(rawEmbedding instanceof List<?> rawList)) {
            throw new IllegalStateException("Embedding recibido no es una lista válida");
        }

        List<?> raw = rawList;
        float[] embedding = new float[raw.size()];

        for (int i = 0; i < raw.size(); i++) {
            embedding[i] = ((Number) raw.get(i)).floatValue();
        }

        return embedding;
    }

    // 2. Guardar embedding como JSON (String)
    public String embeddingToString(float[] embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo serializar el embedding a JSON", e);
        }
    }

    // 3. Leer embedding desde JSON (String)
    public float[] stringToEmbedding(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (Exception e) {
            return null;
        }
    }

    // 4. Similaridad coseno
    public double cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null) return -1.0;
        if (a.length != b.length) return -1.0;

        double dot = 0, normA = 0, normB = 0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) return -1.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // 5. Distancia euclidiana (por si la quieres usar luego)
    public double euclideanDistance(float[] a, float[] b) {
        if (a == null || b == null) return 9999;
        if (a.length != b.length) return 9999;

        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // 6. Buscar mejor coincidencia entre un embedding candidato y todos los registros
    public MatchResult findBestMatch(float[] candidate, List<Registrations> allRegs) {

        Registrations best = null;
        double bestSim = -1.0;

        for (Registrations reg : allRegs) {

            String storedJson = reg.getFace_embedding();
            if (storedJson == null) continue;

            float[] emb = stringToEmbedding(storedJson);
            if (emb == null) continue;

            double sim = cosineSimilarity(candidate, emb);

            if (sim > bestSim) {
                bestSim = sim;
                best = reg;
            }
        }

        return new MatchResult(best, bestSim);
    }

    public record MatchResult(Registrations registration, double similarity) {}
}
