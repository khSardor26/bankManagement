package org.example.email_entity.repository;

import org.example.email_entity.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findBycardNumber(Long cardnum);
    boolean existsByCardNumber(Long cardNum);
}
