package com.miapp.backend.controller;


import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.*;
import org.springframework.web.multipart.MultipartFile;
import com.miapp.backend.utils.MultipartInputStreamFileResource;

@RestController
@RequestMapping("/face")
public class FaceController {

    @PostMapping("/embedding")
    public ResponseEntity<?> getEmbedding(@RequestParam("file") MultipartFile file) throws Exception {

        String url = "https://faceservice-production.up.railway.app/extract";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(url, request, String.class);

        return ResponseEntity.ok(response);
    }
}

