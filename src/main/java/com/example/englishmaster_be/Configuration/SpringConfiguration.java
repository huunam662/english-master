package com.example.englishmaster_be.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.*;

import java.util.Properties;

public class SpringConfiguration {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("testmeusolution@gmail.com");
        mailSender.setPassword("testmeusolution1");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);

        return mailSender;
    }
}
