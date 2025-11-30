package com.shashank.auth_service.service;

import com.shashank.auth_service.domain.Role;
import com.shashank.auth_service.domain.User;
import com.shashank.auth_service.dto.AuthResponse;
import com.shashank.auth_service.dto.LoginRequest;
import com.shashank.auth_service.dto.RegisterRequest;
import com.shashank.auth_service.repo.UserRepository;
import com.shashank.auth_service.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        String token = jwtProvider.generateToken(req.getEmail());
        return new AuthResponse(token, "Bearer");
    }
}
