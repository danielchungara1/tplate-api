package com.tplate.security.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void send(Email email) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email.getTo());

        msg.setSubject(email.getSubject());
        msg.setText(email.getResetCode());

        javaMailSender.send(msg);
    }
}
