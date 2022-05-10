package com.indulgetech.configurations;

import com.indulgetech.services.system.mail.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailSenderConfig {

    @Value("${config.emailSender}")
    private String emailSender;

    @Bean
    public EmailSender emailSender(ApplicationContext context) {

        if (emailSender.equals("default")) {
            return (EmailSender) context.getBean("defaultEmailSenderImpl");
        } else if(emailSender.equals("cloud")) {
            return (EmailSender) context.getBean("sesEmailSender");
        }else{
            return (EmailSender) context.getBean("sesSMTPEmailSender");
        }
    }

}
