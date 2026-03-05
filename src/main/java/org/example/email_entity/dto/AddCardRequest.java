package org.example.email_entity.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AddCardRequest(

        @NotBlank
        Long cardNum,

        @NotBlank
        Long balance,

        @NotBlank
        LocalDate executesAt

) {

}
