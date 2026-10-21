package com.example.demo.DTO.userProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100,
                message = "O nome deve ter entre 2 e 100 caracteres")
        String name

) {
}

