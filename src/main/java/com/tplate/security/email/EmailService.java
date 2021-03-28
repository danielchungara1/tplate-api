package com.tplate.security.email;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Log4j2
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    public void send(Email email) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String templateContent = FreeMarkerTemplateUtils
                    .processTemplateIntoString(freemarkerConfig.getConfiguration()
                                    .getTemplate("/email/reset-password.ftl"),
                            email.getData());

            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(templateContent, true);
            javaMailSender.send(mimeMessage);
            log.info("Email was sent successfully");

        } catch (Exception e) {
            log.error("Email wasn't sent. {}", e.getClass().getCanonicalName());
            throw new MailSendException("Email wasn't sent.");

        }

    }
}
