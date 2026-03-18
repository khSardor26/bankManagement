package org.example.email_entity.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.email_entity.dto.*;
import org.example.email_entity.entity.Card;
import org.example.email_entity.entity.CardStatus;
import org.example.email_entity.entity.User;
import org.example.email_entity.repository.CardRepository;
import org.example.email_entity.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final CardRepository cardRepository;

    @Transactional
    @Override
    public BankResponse addCard(AddCardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cardRepository.existsByCardNumber(request.cardNum())) {
            throw new RuntimeException("Card already linked");
        }

        Card card = Card.builder()
                .cardNumber(request.cardNum())
                .balance(request.balance())
                .executesAt(request.executesAt())
                .status(CardStatus.ACTIVE)
                .build();

        user.addCard(card);

        cardRepository.save(card);

        return BankResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .cardNum(card.getCardNumber())
                .initBalance(card.getBalance())
                .build();
    }


    @Transactional
    @Override
    public void removeCard(Long cardNum) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card targetCard = user.getCards().stream()
                .filter(card -> card.getCardNumber().equals(cardNum))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found"));

        cardRepository.deleteById(targetCard.getId());
        user.removeCard(targetCard);
    }






    @Transactional
    @Override
    public BankResponse deposit(Long amount, Long cardNum){
        if (amount < 0) throw new RuntimeException("Negative amount");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card targetCard = user.getCards().stream()
                .filter(card -> card.getCardNumber().equals(cardNum))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Long initialBalance = targetCard.getBalance();
        targetCard.setBalance(initialBalance + amount);

        EmailBody message = EmailBody.builder()
                .recipient(user.getEmail())
                .subject("------ SUCCESFULLY DEPOSITED ------")
                .body("\nTransaction Time: " + LocalDateTime.now() + "\nFullName: " + user.getFullName()
                + "\nCardNum: " + targetCard.getCardNumber() + "\nAmount: " + amount
                + "\nCurrent Balance: " + targetCard.getBalance()
                )
                .build();

        emailService.sendEmail(message);


        return BankResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .cardNum(targetCard.getCardNumber())
                .initBalance(targetCard.getBalance())
                .build();
        
    }

    @Transactional
    @Override
    public BankResponse withdraw(Long amount, Long cardNum){
        if (amount < 0) throw new RuntimeException("Negative amount");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card targetCard = user.getCards().stream()
                .filter(card -> card.getCardNumber().equals(cardNum))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Long initialBalance = targetCard.getBalance();
        if (initialBalance - amount < 0) throw new RuntimeException("Not enough money");


        targetCard.setBalance(initialBalance - amount);



        //Sending notification
        EmailBody message = EmailBody.builder()
                .recipient(user.getEmail())
                .subject("------ SUCCESFULLY WITHDRAWED ------")
                .body("\nTransaction Time: " + LocalDateTime.now() + "\nFullName: " + user.getFullName()
                        + "\nCardNum: " + targetCard.getCardNumber() + "\nAmount: " + amount
                        + "\nCurrent Balance: " + targetCard.getBalance()
                )
                .build();

        emailService.sendEmail(message);


        return BankResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .cardNum(targetCard.getCardNumber())
                .initBalance(targetCard.getBalance())
                .build();

    }

    @Transactional
    @Override
    public BankResponse transfer(TransferRequest request) {
        if (request.amount() < 0) throw new RuntimeException("Negative amount");


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card fromCard = user.getCards().stream()
                .filter(card -> card.getCardNumber().equals(request.fromCard()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Your card not found"));

        Card toCard = cardRepository.findBycardNumber(request.toCard()).orElseThrow(
                () -> new RuntimeException("Destination card not found"));

        fromCard.setBalance(fromCard.getBalance() - request.amount());
        toCard.setBalance(toCard.getBalance() + request.amount());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);



        EmailBody message1 = EmailBody.builder()
                .recipient(fromCard.getUser().getEmail())
                .subject("------ SUCCESFULLY TRANSFERED ------")
                .body("\nTransaction Time: " + LocalDateTime.now() + "\nFrom: " + fromCard.getCardNumber()
                + "\nTo: " + toCard.getCardNumber() + "\nAmount: " + request.amount()
                                + "\nCurrent Balance: " + fromCard.getBalance()

                )
                .build();


        EmailBody message2 = EmailBody.builder()
                .recipient(toCard.getUser().getEmail())
                .subject("------ MONEY WAS RECEIVED ------")
                .body("\nTransaction Time: " + LocalDateTime.now() + "\nFrom: " + fromCard.getCardNumber()
                        + "\nTo: " + toCard.getCardNumber() + "\nAmount: " + request.amount()
                        + "\nCurrent Balance: " + toCard.getBalance()

                )
                .build();

        emailService.sendEmail(message1);
        emailService.sendEmail(message2);

        return BankResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .cardNum(fromCard.getCardNumber())
                .initBalance(fromCard.getBalance())
                .build();

    }



    @Transactional
    @Override
    public UserWithCardsResponse getMeWithCards() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmailWithCards(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CardResponse> cards = user.getCards().stream()
                .map(c -> new CardResponse(
                        c.getCardNumber(),
                        c.getBalance(),
                        c.getStatus().name()
                ))
                .toList();

        return new UserWithCardsResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                cards
        );
    }


}
