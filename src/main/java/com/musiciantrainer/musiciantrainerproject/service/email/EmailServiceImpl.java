package com.musiciantrainer.musiciantrainerproject.service.email;

import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private Environment env;
    private JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(Environment env, JavaMailSender mailSender) {
        this.env = env;
        this.mailSender = mailSender;
    }

    @Override
    public void sendNewMail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public SimpleMailMessage constructEmail(String subject, String body, User theUser) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(theUser.getEmail());
        email.setFrom(env.getProperty("support.email"));

        return email;
    }

}
