package com.miapp.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DniService {

    @Value("${apiperu.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getDniData(String dni) {

        String url = "https://apiperu.dev/api/dni";

        // Esta vez el cuerpo SÍ es correcto:
        Map<String, Object> body = Map.of("dni", dni);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody();
        }
        catch (HttpClientErrorException e) {
            System.out.println("❌ Error API Perú: " + e.getStatusCode());
            System.out.println("❌ Respuesta: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error consultando DNI");
        }
    }
}
