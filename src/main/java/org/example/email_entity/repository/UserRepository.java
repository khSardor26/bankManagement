package org.example.email_entity.repository;

import org.example.email_entity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u left join fetch u.cards where u.email = :email")
    Optional<User> findByEmailWithCards(@Param("email") String email);


    @Query("""
           SELECT DISTINCT u
           FROM User u
           LEFT JOIN FETCH u.cards
           """)
    Page<User> findAllUsersWithCards(Pageable pageable);
}
