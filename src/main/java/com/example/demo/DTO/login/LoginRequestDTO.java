package com.example.demo.DTO.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        
        @NotBlank(message = "Informe seu E-mail")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Informe sua senha")
        String password) {
}
