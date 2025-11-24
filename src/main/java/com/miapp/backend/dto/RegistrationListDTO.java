package com.miapp.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RegistrationListDTO {
    private UUID id;
    private String dni;
    private String full_name;

    private String department;
    private String province;
    private String district;

    private String photo_path;
}
