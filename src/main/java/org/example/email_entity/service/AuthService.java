package org.example.email_entity.service;

import org.apache.coyote.BadRequestException;
import org.example.email_entity.dto.AuthResponse;
import org.example.email_entity.dto.LoginRequest;
import org.example.email_entity.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request) throws BadRequestException;
}
