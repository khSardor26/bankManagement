package org.example.email_entity.service;


import lombok.RequiredArgsConstructor;
import org.example.email_entity.dto.AdminUpdateCard;
import org.example.email_entity.entity.Card;
import org.example.email_entity.entity.User;
import org.example.email_entity.repository.CardRepository;
import org.example.email_entity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Override
    public Card updateCard(AdminUpdateCard request){
        Card card = cardRepository.findBycardNumber(request.cardNum()).orElseThrow(
                () -> new RuntimeException("Card not found"));

        if (request.status() != null) card.setStatus(request.status());
        cardRepository.save(card);

        return card;
    }

    @Override
    public Page<User> getAllUsersWithCard(Pageable pageable) {
        return userRepository.findAllUsersWithCards(pageable);
    }



}
