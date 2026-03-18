package org.example.email_entity.service;

import org.example.email_entity.dto.AddCardRequest;
import org.example.email_entity.dto.BankResponse;
import org.example.email_entity.dto.TransferRequest;
import org.example.email_entity.dto.UserWithCardsResponse;

public interface UserService {
    BankResponse addCard(AddCardRequest request);

    void removeCard(Long cardNum);

    BankResponse deposit(Long amount, Long cardNum);

    BankResponse withdraw(Long amount, Long cardNum);

    BankResponse transfer(TransferRequest request);

    UserWithCardsResponse getMeWithCards();
}
