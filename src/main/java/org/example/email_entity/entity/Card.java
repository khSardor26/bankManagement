package org.example.email_entity.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Card {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, nullable = false, unique = true)
    private Long cardNumber;

    @Column(length = 10000000)
    private Long balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;


    @Column(nullable = false)
    private LocalDate executesAt;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // IMPORTANT
    private User user;


}
