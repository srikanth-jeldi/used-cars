package com.epitomehub.carverse.notificationservice.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.hostinger.com");
        mailSender.setPort(465);
        mailSender.setUsername("contactus@epitomehub.com\n");   // full email
        mailSender.setPassword("Intelcorei_5");          // real pass

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.hostinger.com");
        // if you ever use 587 instead:
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
