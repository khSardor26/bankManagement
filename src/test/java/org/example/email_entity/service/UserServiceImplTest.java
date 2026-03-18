package org.example.email_entity.service;

import org.example.email_entity.dto.UserWithCardsResponse;
import org.example.email_entity.entity.Card;
import org.example.email_entity.entity.CardStatus;
import org.example.email_entity.entity.Roles;
import org.example.email_entity.entity.User;
import org.example.email_entity.repository.CardRepository;
import org.example.email_entity.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deposit_negativeAmount_throws() {
        assertThatThrownBy(() -> userService.deposit(-1L, 123L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Negative amount");

        verifyNoInteractions(userRepository, cardRepository, emailService);
    }

    @Test
    void getMeWithCards_returnsCurrentUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .fullName("User One")
                .role(Roles.USER)
                .build();

        Card card = Card.builder()
                .id(10L)
                .cardNumber(8600123412341234L)
                .balance(1000L)
                .status(CardStatus.ACTIVE)
                .executesAt(LocalDate.of(2028, 12, 31))
                .build();

        user.addCard(card);

        when(userRepository.findByEmailWithCards("user@example.com")).thenReturn(Optional.of(user));

        UserWithCardsResponse response = userService.getMeWithCards();

        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.cards()).hasSize(1);
        assertThat(response.cards().get(0).cardNumber()).isEqualTo(8600123412341234L);
    }
}
