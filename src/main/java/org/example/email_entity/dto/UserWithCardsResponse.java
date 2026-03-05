package org.example.email_entity.dto;

import org.example.email_entity.entity.Roles;

public record UserWithCardsResponse(
        Long id,
        String email,
        String fullName,
        Roles role,
        java.util.List<CardResponse> cards
) {}
