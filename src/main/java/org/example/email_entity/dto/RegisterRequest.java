package org.example.email_entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.email_entity.entity.Roles;

public record RegisterRequest(

        @NotBlank
        @Email(message = "Not valid email format")
        @Size(max = 100)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String fullName,

        @NotBlank
        @Size(min = 6, max = 50)
        String password,

        @NotNull(message = "The Role must be set")
        Roles role
) {
}
