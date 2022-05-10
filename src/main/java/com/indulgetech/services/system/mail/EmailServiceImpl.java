package com.indulgetech.services.system.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indulgetech.services.system.SystemConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static constants.SchemaConstant.*;

@RequiredArgsConstructor
@Service("emailService")
public class EmailServiceImpl implements EmailService {

    private final EmailSender emailSender;
    private final SystemConfigurationService systemConfigurationService;

    @Override
    public void sendEmail(Email email) {

    }

    @Override
    @Async
    public void sendAsyncEmail(Email email) {

        EmailConfig emailConfig = this.getEmailConfiguration();
        if (emailConfig != null) {
            this.emailSender.setEmailConfig(emailConfig);
        }
        this.emailSender.send(email);

    }

    private EmailConfig getEmailConfiguration() {

        String value = this.systemConfigurationService.findConfigValueByKey(EMAIL_CONFIG);
        ObjectMapper mapper = new ObjectMapper();
        EmailConfig emailConfig = null;
        try {
            emailConfig = mapper.readValue(value, EmailConfig.class);
        } catch (Exception e) {
            // throw new ServiceException("Cannot parse json string " + value);
        }
        return emailConfig;
    }


}
