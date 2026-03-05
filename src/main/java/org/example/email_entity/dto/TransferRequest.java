package org.example.email_entity.dto;

public record TransferRequest(
        Long fromCard,
        Long toCard,
        Long amount


) {
}
