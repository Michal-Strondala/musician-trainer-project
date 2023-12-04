package com.musiciantrainer.musiciantrainerproject.service.email;

public interface EmailService {
    void sendNewMail(String to, String subject, String body);
}
