package org.example.email_entity.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.example.email_entity.dto.AuthResponse;
import org.example.email_entity.dto.EmailBody;
import org.example.email_entity.dto.LoginRequest;
import org.example.email_entity.dto.RegisterRequest;
import org.example.email_entity.entity.Roles;
import org.example.email_entity.entity.User;
import org.example.email_entity.repository.UserRepository;
import org.example.email_entity.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtUtil jwtService;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());


        return new AuthResponse(accessToken, "Bearer");
    }


    @Transactional
    public AuthResponse register(RegisterRequest request) throws BadRequestException {
        User user = User.builder()
                .email(request.email().toLowerCase().trim())
                .fullName(request.fullName().trim())
                .role(request.role())
                .password(passwordEncoder.encode(request.password()))
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("User already exists");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(accessToken, "Bearer");
    }





}
