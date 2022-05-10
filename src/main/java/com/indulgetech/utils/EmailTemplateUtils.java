package com.indulgetech.utils;


import com.indulgetech.constants.Constants;
import com.indulgetech.constants.EmailConstants;
import com.indulgetech.models.users.User;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.services.system.SystemConfigurationService;
import com.indulgetech.services.system.mail.Email;
import com.indulgetech.services.system.mail.EmailService;
import com.indulgetech.services.system.mail.templateEngine.TemplateEngine;
import constants.SchemaConstant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class EmailTemplateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateUtils.class);
    private final static String LOGOPATH = "LOGOPATH";

    private final EmailService emailService;
    private final SystemConfigurationService systemConfigurationService;
    private final TemplateEngine templateEngine;

    @Value("${api.url-domain}")
    private String urlDomain;

    private final static String EMAIL_FOOTER_COPYRIGHT = "EMAIL_FOOTER_COPYRIGHT";
    private final static String EMAIL_DISCLAIMER = "EMAIL_DISCLAIMER";
    private final static String EMAIL_SPAM_DISCLAIMER = "EMAIL_SPAM_DISCLAIMER";
    private final static String EMAIL_FROM_EMAIL = "EMAIL_FROM_EMAIL";


    public void sendCreatePasswordUrlLinkEmail(ClientUser clientUser, String passwordUrl) {

        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(Constants.CREATE_PASSWORD_URL, passwordUrl);
        templateTokens.put(EmailConstants.EMAIL_FULLNAME,clientUser.getLastName()+" "+clientUser.getFirstName());

        String subject = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.EMAIL_SUBJ_CREATE_PSSWORD_EMAIL);
        if (StringUtils.isEmpty(subject)) {
            subject = SchemaConstant.DEFAULT_CREATE_PASSWORD_EMAIL_SUBJ;
            LOGGER.warn("Found no Create password email subject defined in configuration. Using application defined subject:" + subject + " as a fallback");
        }

        Email email = this.createEmail(clientUser.getEmail(), subject, EmailConstants.EMAIL_CREATE_PASSWORD_TMPL, templateTokens);
        this.emailService.sendAsyncEmail(email);
    }


    public void sendClientPasswordCreatedEmail(ClientUser clientUser, String clientUrl) {

        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EmailConstants.CLIENT_URL, clientUrl+"/login");
        templateTokens.put(EmailConstants.EMAIL_USER_NAME, clientUser.getEmail());
        templateTokens.put(EmailConstants.EMAIL_FULLNAME,clientUser.getLastName()+" "+clientUser.getFirstName());
        String subject = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.EMAIL_SUBJ_PASSWORD_CREATED_MAIL);
        if (StringUtils.isEmpty(subject)) {
            subject = SchemaConstant.DEFAULT_PASSWORD_CREATED_MAIL_SUBJ;
            LOGGER.warn("Found no password created email subject defined in configuration. Using application defined subject:" + subject + " as a fallback");
        }
        Email email = this.createEmail(clientUser.getEmail(), subject, EmailConstants.EMAIL_PASSWORD_CREATED_TMPL, templateTokens);
        this.emailService.sendAsyncEmail(email);
    }

    public void sendCreateAdminUserEmail(AdminUser adminUser, String plainPassword) {
        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EmailConstants.EMAIL_USER_PASSWORD, plainPassword);
        templateTokens.put(EmailConstants.EMAIL_FULLNAME,adminUser.getLastName()+" "+adminUser.getFirstName());
        templateTokens.put(EmailConstants.EMAIL_USER_NAME,adminUser.getEmail());
        templateTokens.put("APP_URL",urlDomain+"/login");

        String subject = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.EMAIL_SUBJ_CREATE_ADMIN_USER_EMAIL);
        if (StringUtils.isEmpty(subject)) {
            subject = SchemaConstant.DEFAULT_CREATE_ADMIN_USER_EMAIL_SUBJ;
            LOGGER.warn("Found no Create password email subject defined in configuration. Using application defined subject:" + subject + " as a fallback");
        }

        Email email = this.createEmail(adminUser.getEmail(), subject, EmailConstants.EMAIL_CREATE_ADMIN_USER_TMPL, templateTokens);
        this.emailService.sendAsyncEmail(email);
    }

    public void sendCreateClientUserEmail(ClientUser organizationUser , String plainPassword) {
        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EmailConstants.EMAIL_USER_PASSWORD, plainPassword);
        templateTokens.put(EmailConstants.EMAIL_FULLNAME,organizationUser.getLastName()+" "+organizationUser.getFirstName());
        templateTokens.put(EmailConstants.EMAIL_USER_NAME,organizationUser.getEmail());
        templateTokens.put("APP_URL",urlDomain+"/login");
        String subject = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.EMAIL_SUBJ_CREATE_ADMIN_USER_EMAIL);
        if (StringUtils.isEmpty(subject)) {
            subject = SchemaConstant.DEFAULT_CREATE_ADMIN_USER_EMAIL_SUBJ;
            LOGGER.warn("Found no Create password email subject defined in configuration. Using application defined subject:" + subject + " as a fallback");
        }

        Email email = this.createEmail(organizationUser.getEmail(), subject, EmailConstants.EMAIL_CREATE_ORGANISATION_USER_TMPL, templateTokens);
        this.emailService.sendAsyncEmail(email);

    }

    public void sendPasswordResetEmail(User user, String passwordResetUrl) {

        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EmailConstants.EMAIL_PASSWORD_RESET_URL, passwordResetUrl);
        templateTokens.put(EmailConstants.EMAIL_FULLNAME,user.getLastName()+" "+user.getFirstName());
        String subject = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.EMAIL_SUBJ_CREATE_PSSWORD_EMAIL);
        if (StringUtils.isEmpty(subject)) {
            subject = SchemaConstant.DEFAULT_PASSWORD_RESET_EMAIL_SUBJ;
            LOGGER.warn("Found no password reset email subject defined in configuration. Using application defined subject:" + subject + " as a fallback");
        }

        Email email = this.createEmail(user.getEmail(), subject, EmailConstants.EMAIL_PASSWORD_RESET_TMPL, templateTokens);
        this.emailService.sendAsyncEmail(email);
    }
    /**
     * Builds generic email information
     *
     * @return
     */
    private Map<String, String> createEmailObjectsMap() {

        String companyName=systemConfigurationService.findConfigValueByKey(SchemaConstant.COMPANY_NAME);
        if(StringUtils.isEmpty(companyName)){
          LOGGER.warn("No Company name found in configuration,using default application defined value");
            companyName=SchemaConstant.DEFAULT_COMPANY_NAME;
        }
        String[] copyArg = {companyName, CustomDateUtils.getPresentYear()};

        String supportEmail=systemConfigurationService.findConfigValueByKey(SchemaConstant.SUPPORT_EMAIL);
        if(StringUtils.isEmpty(supportEmail)){
            LOGGER.warn("No Support email found in configuration,using default application defined value");
            supportEmail=SchemaConstant.DEFAULT_SUPPORT_EMAIL;
        }
        String[] supportEmailArg = {supportEmail};

        String emailDisclaimer = "This email address was given to us by you or by one of our customers. If you feel that you have received this email in error, please send an email to " + supportEmailArg[0] + " for de-activation";
        String emailSpamDisclaimer = "This email is sent in accordance with the US CAN-SPAM Law in effect 2004-01-01. Removal requests can be sent to this address and will be honored and respected";

        Map<String, String> templateTokens = new HashMap<>();

        templateTokens.put(LOGOPATH, "");
        templateTokens.put(EMAIL_FOOTER_COPYRIGHT, "Copyright @ " + copyArg[0] + " " + copyArg[1] + ", All Rights Reserved");
        templateTokens.put(EMAIL_DISCLAIMER, emailDisclaimer);
        templateTokens.put(EMAIL_SPAM_DISCLAIMER, emailSpamDisclaimer);
        templateTokens.put(EMAIL_FROM_EMAIL, supportEmail);

        return templateTokens;
    }

    private Email createEmail(String to, String subject, String template, Map<String, String> templateTokens) {

        Email email = new Email();

        String companyName = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.COMPANY_NAME);
        if (StringUtils.isEmpty(companyName)) {
            companyName = SchemaConstant.DEFAULT_COMPANY_NAME;
            LOGGER.warn("Found no Company Name defined in configuration. Using application defined name:" + companyName + " as a fallback");
        }

        /*String companyEmail = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.COMPANY_EMAIL);
        if (!CommonUtils.isValidEmail(companyEmail)) {
            LOGGER.warn("Found invalid Company Email:'" + companyEmail + "' defined in configuration. Using application defined email:" + SchemaConstant.DEFAULT_COMPANY_EMAIL + " as a fallback");
            companyEmail = SchemaConstant.DEFAULT_COMPANY_EMAIL;
        }*/
        email.setFrom(companyName);
        email.setFromEmail(templateTokens.get(EMAIL_FROM_EMAIL));
        email.setSubject(subject);
        email.setTo(to);
        email.setBody(this.templateEngine.processTemplateIntoString(template, templateTokens));
        return email;
    }


}
