package com.example.demo.service;

import com.example.demo.DTO.login.LoginRequestDTO;
import com.example.demo.DTO.login.LoginResponseDTO;
import com.example.demo.DTO.register.RegisterRequestDTO;
import com.example.demo.DTO.register.RegisterResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService implements UserDetailsService {


    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.existsByEmail(registerRequestDTO.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(registerRequestDTO.password());

        User user = new User(registerRequestDTO.name(), registerRequestDTO.email(), encodedPassword);

        User savedUser = userRepository.save(user);

        return new RegisterResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail());

    }


    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado")
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.email())
                .orElseThrow(() ->
                        new BadCredentialsException("E-mail inválido"));

        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Senha inválida");
        }

        String token = jwtService.generateToken(loginRequestDTO.email());

        return new LoginResponseDTO(token);
    }

}
