package org.example.email_entity.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String bearer) {
}
