package org.example.email_entity.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.email_entity.entity.CardStatus;

public record AdminUpdateCard(
        @NotBlank
        Long cardNum,

        CardStatus status
        ) {
}
