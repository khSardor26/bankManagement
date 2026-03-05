package org.example.email_entity.controller;


import lombok.RequiredArgsConstructor;
import org.example.email_entity.dto.*;
import org.example.email_entity.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class BankSimulator {

    @Autowired
    private final UserServiceImpl userService;

    @PostMapping("users/user/addCard")
    public ResponseEntity<BankResponse> addCard(@RequestBody AddCardRequest request){
        return new ResponseEntity<>(userService.addCard(request),HttpStatus.OK);
    }

    @PatchMapping("users/user/deposit")
    public ResponseEntity<BankResponse> depositMoney(@RequestBody UpdateBalance request){
        Long amount = request.amount();
        Long cardNum = request.cardNum();

        return new ResponseEntity<>(userService.deposit(amount, cardNum), HttpStatus.OK);

    }

    @PatchMapping("users/user/withdraw")
    public ResponseEntity<BankResponse> withDrawMoney(@RequestBody UpdateBalance request){
        Long amount = request.amount();
        Long cardNum = request.cardNum();
        return new ResponseEntity<>(userService.withdraw(amount, cardNum), HttpStatus.OK);
    }

    @PatchMapping("users/user/transfer")
    public ResponseEntity<BankResponse> transfer(@RequestBody TransferRequest request){
        return new ResponseEntity<>(userService.transfer(request), HttpStatus.OK);
    }

    @GetMapping("users/user/me")
    public ResponseEntity<UserWithCardsResponse> getAll(){
        return new ResponseEntity<>(userService.getMeWithCards(),HttpStatus.OK);

    }

    @DeleteMapping("users/user/delete/{cardNum}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardNum){
        userService.removeCard(cardNum);
        return ResponseEntity.noContent().build();
    }

}
