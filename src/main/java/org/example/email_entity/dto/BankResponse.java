package org.example.email_entity.dto;

import lombok.Builder;


@Builder
public record BankResponse(
         String fullName,
         String email,
         Long cardNum,
         Long initBalance

) {
}
