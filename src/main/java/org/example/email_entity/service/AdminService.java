package org.example.email_entity.service;

import org.example.email_entity.dto.AdminUpdateCard;
import org.example.email_entity.entity.Card;
import org.example.email_entity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Card updateCard(AdminUpdateCard request);

    Page<User> getAllUsersWithCard(Pageable pageable);
}
