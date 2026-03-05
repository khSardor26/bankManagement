package org.example.email_entity.controller;


import lombok.RequiredArgsConstructor;
import org.example.email_entity.dto.AdminUpdateCard;
import org.example.email_entity.entity.Card;
import org.example.email_entity.entity.User;
import org.example.email_entity.service.AdminServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AdminServiceImpl adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = adminService.getAllUsersWithCard(pageable);

        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/update")
    public ResponseEntity<Card> updateCard(@RequestBody AdminUpdateCard request){
        return new ResponseEntity<>(adminService.updateCard(request), HttpStatus.OK);
    }


}
