package com.indulgetech.unit.service.system.email;

import com.indulgetech.AbstractTest;
import com.indulgetech.services.system.SystemConfigurationService;
import com.indulgetech.services.system.mail.Email;
import com.indulgetech.services.system.mail.EmailConfig;
import com.indulgetech.services.system.mail.EmailSender;
import com.indulgetech.services.system.mail.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    /**
     * System under test (SUT)
     */
    @InjectMocks
    EmailServiceImpl emailService;

    @Mock
    SystemConfigurationService systemConfigurationService;

    @Mock
    EmailSender emailSender;

    @Test
    void shouldSendEmailSuccessfully() throws Exception {
        //arrange
        EmailConfig emailConfig=new EmailConfig();
        emailConfig.setPassword("");
        emailConfig.setUsername("");
        emailConfig.setSmtpAuth(true);
        //act
        //input
        Email email = new Email();
        email.setFrom("Indulge");
        email.setFromEmail("noreply@indulgetech.com");
        email.setSubject("subject");
        email.setTo("fabulousval4ril@gmail.com");
        email.setBody("body");
        //find email config
        when(this.systemConfigurationService.findConfigValueByKey(anyString())).thenReturn(emailConfig.toJSONString());
        doNothing().when(this.emailSender).setEmailConfig(any(EmailConfig.class));
        doNothing().when(this.emailSender).send(any(Email.class));
        this.emailService.sendAsyncEmail(email);
        verify(this.emailSender).send(any(Email.class));
        verify(this.emailSender).setEmailConfig(any(EmailConfig.class));
    }

    @Test
    void shouldSendEmailSuccessfullyWhenSendEmailWithNoUserDefinedConfig() throws Exception {
        //arrange
        EmailConfig emailConfig=new EmailConfig();
        emailConfig.setPassword("");
        emailConfig.setUsername("");
        emailConfig.setSmtpAuth(true);
        //act
        //input
        Email email = new Email();
        email.setFrom("Indulge");
        email.setFromEmail("noreply@indulgetech.com");
        email.setSubject("subject");
        email.setTo("fabulousval4ril@gmail.com");
        email.setBody("body");
        //find email config
        when(this.systemConfigurationService.findConfigValueByKey(anyString())).thenReturn(null);
        doNothing().when(this.emailSender).send(any(Email.class));
        this.emailService.sendAsyncEmail(email);
        verify(this.emailSender).send(any(Email.class));
        verify(this.emailSender,never()).setEmailConfig(any(EmailConfig.class));
    }


}
