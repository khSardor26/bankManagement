package org.example.email_entity.service;

import org.apache.coyote.BadRequestException;
import org.example.email_entity.dto.AuthResponse;
import org.example.email_entity.dto.LoginRequest;
import org.example.email_entity.dto.RegisterRequest;
import org.example.email_entity.entity.Roles;
import org.example.email_entity.entity.User;
import org.example.email_entity.repository.UserRepository;
import org.example.email_entity.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_returnsJwtToken() {
        LoginRequest request = new LoginRequest("user@example.com", "secret");
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .fullName("User One")
                .role(Roles.USER)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user@example.com", "USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.bearer()).isEqualTo("Bearer");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void register_throwsBadRequestWhenUserExists() {
        RegisterRequest request = new RegisterRequest("user@example.com", "User One", "secret", Roles.USER);

        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User already exists");
    }
}
