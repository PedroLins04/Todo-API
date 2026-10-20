package com.example.demo.controller;

import com.example.demo.DTO.login.LoginRequestDTO;
import com.example.demo.DTO.login.LoginResponseDTO;
import com.example.demo.DTO.register.RegisterRequestDTO;
import com.example.demo.DTO.register.RegisterResponseDTO;
import com.example.demo.DTO.userProfile.UserProfileDTO;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.register(registerRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.findProfile(authentication.getName()));
    }
}
