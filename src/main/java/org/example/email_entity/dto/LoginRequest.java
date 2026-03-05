package org.example.email_entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginRequest(
        @NotBlank
        @Email(message = "Not valid email format")
        @Size(max = 100)
        String email,

        @NotBlank
        @Size(min = 6, max = 50)
        String password
) {
}
