package com.service.authentication.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j(topic = "MAIL-SERVICE")
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sending Simple Emails
     *
     * @param from
     * @param to
     * @param subject
     * @param body
     * @throws MessagingException
     */
    public void sendEmail(String from, String to, String subject, String body) {
        log.info("Sending email to {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    /**
     * Sending Emails With Attachments
     *
     * @param from
     * @param to
     * @param subject
     * @param body
     * @param pathFile
     * @throws MessagingException
     */
    public void sendEmail(String from, String to, String subject, String body, String pathFile) throws MessagingException {
        log.info("Sending email to {}", to);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        if (StringUtils.hasLength(pathFile)) {
            FileSystemResource file = new FileSystemResource(new File(pathFile));
            helper.addAttachment("Attach file", file);
        }

        emailSender.send(message);
    }
}
