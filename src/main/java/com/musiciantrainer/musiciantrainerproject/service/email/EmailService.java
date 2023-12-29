package com.musiciantrainer.musiciantrainerproject.service.email;

import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendNewMail(String to, String subject, String body);

    SimpleMailMessage constructEmail(String subject, String body, User theUser);
}
