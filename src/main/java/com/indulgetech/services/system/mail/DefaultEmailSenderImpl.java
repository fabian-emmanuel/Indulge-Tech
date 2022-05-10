package com.indulgetech.services.system.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

@RequiredArgsConstructor
@Component("defaultEmailSenderImpl")
@Slf4j
public class DefaultEmailSenderImpl implements EmailSender {

//    private static final Logger log = LoggerFactory.getLogger(DefaultEmailSenderImpl.class);

    private final JavaMailSender emailSender;
    private  EmailConfig emailConfig;


    @Override
    public void send(Email email){
        
        log.info("Begin sending email ");
        final String eml = email.getFrom();
        final String from = email.getFromEmail();
        final String to = email.getTo();
        final String subject = email.getSubject();

        MimeMessagePreparator preparator = mimeMessage -> {

            JavaMailSenderImpl impl = (JavaMailSenderImpl) emailSender;
            // if email configuration is present in Database, use the same
            if (emailConfig != null) {
                impl.setProtocol(emailConfig.getProtocol());
                impl.setHost(emailConfig.getHost());
                impl.setPort(Integer.parseInt(emailConfig.getPort()));
                impl.setUsername(emailConfig.getUsername());
                impl.setPassword(emailConfig.getPassword());

                Properties prop = new Properties();
                prop.put("mail.smtp.auth", emailConfig.isSmtpAuth());
                prop.put("mail.smtp.starttls.enable", emailConfig.isStarttls());
                impl.setJavaMailProperties(prop);
            }

            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            InternetAddress inetAddress = new InternetAddress();

            inetAddress.setPersonal(eml);
            inetAddress.setAddress(from);

            mimeMessage.setFrom(inetAddress);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(email.getBody(), "utf-8", "html");

            // if(attachment!=null) {
            // MimeMessageHelper messageHelper = new
            // MimeMessageHelper(mimeMessage, true);
            // messageHelper.addAttachment(attachmentFileName, attachment);
            // }

        };
        emailSender.send(preparator);
    }

    @Override
    public void setEmailConfig(EmailConfig emailConfig) {
       this.emailConfig=emailConfig;
    }
}
