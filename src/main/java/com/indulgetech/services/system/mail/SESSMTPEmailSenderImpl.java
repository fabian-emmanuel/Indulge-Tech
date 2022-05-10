package com.indulgetech.services.system.mail;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component("sesSMTPEmailSender")
@Slf4j
public class SESSMTPEmailSenderImpl implements EmailSender {

//    private static final Logger log = LoggerFactory.getLogger(SESSMTPEmailSenderImpl.class);

    // The name of the Configuration Set to use for this message.
    // If you comment out or remove this variable, you will also need to
    // comment out or remove the header below.
    //static final String CONFIGSET = "ConfigSet";

    // Amazon SES SMTP host name. This example uses the US West (Oregon) region.
    // See https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
    // for more information.
    @Value("${amazonProperties.smtpHost}")
    private String SMTP_HOST;

    // Replace smtp_username with your Amazon SES SMTP user name.
    @Value("${amazonProperties.smtpUserName}")
    private String SMTP_USERNAME;

    // Replace smtp_password with your Amazon SES SMTP password.
    @Value("${amazonProperties.smtpPassword}")
    private String SMTP_PASSWORD;

    // The port you will connect to on the Amazon SES SMTP endpoint.
    static final int PORT = 587;

    @Override
    public void send(Email email) {

        Transport transport = null;
            log.info("Sending...");
            // Create a Properties object to contain connection configuration information.
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            // Create a Session object to represent a mail session with the specified properties.
            Session session = Session.getDefaultInstance(props);

        try {
            // Create a message with the specified information.
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(email.getFromEmail(), email.getFrom()));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
            msg.setSubject(email.getSubject());
            msg.setContent(email.getBody(), "text/html");

            // Add a configuration set header. Comment or delete the
            // next line if you are not using a configuration set
            //msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);

            // Create a transport.
            transport = session.getTransport();

            // Send the message.

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(SMTP_HOST, SMTP_USERNAME, SMTP_PASSWORD);

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            // Close and terminate the connection.
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (MessagingException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    @Override
    public void setEmailConfig(EmailConfig emailConfig) {

    }
}
