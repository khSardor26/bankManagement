package org.example.email_entity.dto;


import lombok.Builder;

@Builder
public record EmailBody(
         String recipient,
         String body,
         String subject,
         String attechment
) {
}
