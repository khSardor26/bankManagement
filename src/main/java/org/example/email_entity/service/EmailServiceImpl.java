package org.example.email_entity.service;

import lombok.RequiredArgsConstructor;
import org.example.email_entity.dto.EmailBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public void sendEmail(EmailBody email){

       try{
           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(emailSender);
           message.setTo(email.recipient());
           message.setSubject(email.subject());
           message.setText(email.body());

           javaMailSender.send(message);
       }
       catch(Exception e){
           throw new RuntimeException("Failed to send email");
       }


    }
}
