package com.indulgetech.services.system;


import com.indulgetech.dto.system.ConfigurationMetadata;
import com.indulgetech.dto.system.SystemConfigurationDto;
import com.indulgetech.models.system.SystemConfiguration;
import com.indulgetech.repositories.system.SystemConfigurationRepository;
import com.indulgetech.services.generic.BaseEntityServiceImpl;
import com.indulgetech.services.system.mail.EmailConfig;
import constants.SchemaConstant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static constants.SchemaConstant.*;

@RequiredArgsConstructor
@Service
public class SystemConfigurationServiceImpl extends BaseEntityServiceImpl<Long, SystemConfiguration> implements SystemConfigurationService {

    private static final String GENERAL = "general";
    private static final String EMAIL = "email";
    private static final String CORE = "core";

    private SystemConfigurationRepository repository;
    private Environment env;

    @Autowired
    public SystemConfigurationServiceImpl(SystemConfigurationRepository repository, Environment env) {
        super(repository);
        this.repository = repository;
        this.env = env;
    }

    @Override
    public String findConfigValueByKey(String key) {
        return this.repository.findByConfigurationKey(key);
//        return optional.map(SystemConfiguration::getValue).orElse("");
    }

    @Override
    public void createDefaultConfigurations() {

        Collection<SystemConfiguration> systemConfigurations = this.repository.findAll();
        Map<String, SystemConfiguration> mapByKey = this.mapConfigurationsByKey(systemConfigurations);

        SystemConfiguration configuration = null;
        //company name
        configuration = mapByKey.get(COMPANY_NAME);
        if (configuration == null) {
            configuration = new SystemConfiguration();
        }
        configuration.setConfigurationKey(COMPANY_NAME);
        configuration.setConfigurationName("Company Name");
        configuration.setValue("Decagon Digital Learning Institute");
        configuration.setConfigurationGroup(GENERAL);
        configuration.setSortOrder(2);
        this.create(configuration);


        //app name
        if (!mapByKey.containsKey(APP_NAME)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(APP_NAME);
            configuration.setConfigurationName("Application Name");
            configuration.setValue(DEFAULT_APP_NAME);
            configuration.setConfigurationGroup(GENERAL);
            configuration.setSortOrder(2);
            this.create(configuration);
        }
        //password reset validity term
        if (!mapByKey.containsKey(PSSWORD_SETTING_TOKEN_VLDTY_TRM)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(PSSWORD_SETTING_TOKEN_VLDTY_TRM);
            configuration.setConfigurationName("Password reset token validity term");
            configuration.setValue(String.valueOf(DEFAULT_PWRD_SETTING_VLDTY_TRM));
            configuration.setDescription("Maximum no of hours reset token should be active before expired. Default to" +
                    " 168 (7 days)");
            configuration.setConfigurationGroup(GENERAL);
            configuration.setSortOrder(3);
            this.create(configuration);
        }

        //hear about us
        if (!mapByKey.containsKey(HEAR_ABT_US)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(HEAR_ABT_US);
            configuration.setConfigurationName("Hear about us");
            configuration.setDescription("Set up for hear about us items. Each hear about us item must be seperated by comma");
            configuration.setValue("Web,Newspaper,Television");
            configuration.setConfigurationGroup(GENERAL);
            configuration.setSortOrder(4);
            this.create(configuration);
        }

        //emails
        if (!mapByKey.containsKey(SUPPORT_EMAIL)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(SUPPORT_EMAIL);
            configuration.setConfigurationName("Support email");
            configuration.setValue(DEFAULT_SUPPORT_EMAIL);
            configuration.setDescription("Support email use for support and other administrative purposes");
            configuration.setConfigurationGroup(EMAIL);
            configuration.setSortOrder(1);
            this.create(configuration);
        }

        //email subjects
        if (!mapByKey.containsKey(EMAIL_SUBJ_CREATE_ADMIN_USER_EMAIL)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(EMAIL_SUBJ_CREATE_ADMIN_USER_EMAIL);
            configuration.setConfigurationName("Create admin user email subject");
            configuration.setValue(DEFAULT_CREATE_ADMIN_USER_EMAIL_SUBJ);
            configuration.setConfigurationGroup(EMAIL);
            configuration.setSortOrder(3);
            this.create(configuration);
        }

        if (!mapByKey.containsKey(EMAIL_SUBJ_CREATE_PSSWORD_EMAIL)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(EMAIL_SUBJ_CREATE_PSSWORD_EMAIL);
            configuration.setConfigurationName("Create password email subject");
            configuration.setValue(DEFAULT_CREATE_PASSWORD_EMAIL_SUBJ);
            configuration.setConfigurationGroup(EMAIL);
            configuration.setSortOrder(4);
            this.create(configuration);
        }

        if (!mapByKey.containsKey(EMAIL_SUBJ_PASSWORD_CREATED_MAIL)) {
            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(EMAIL_SUBJ_PASSWORD_CREATED_MAIL);
            configuration.setConfigurationName("Password created email subject");
            configuration.setValue(DEFAULT_PASSWORD_CREATED_MAIL_SUBJ);
            configuration.setConfigurationGroup(EMAIL);
            configuration.setSortOrder(6);
            this.create(configuration);
        }


        //email config
        if (!mapByKey.containsKey(EMAIL_CONFIG)) {
            EmailConfig emailConfig = new EmailConfig();
            emailConfig.setHost(env.getProperty("spring.mail.host"));
            emailConfig.setProtocol(env.getProperty("spring.mail.properties.mail.transport.protocol"));
            emailConfig.setPort(env.getProperty("spring.mail.properties.mail.smtp.port"));
            emailConfig.setSmtpAuth(Objects.equals(env.getProperty("spring.mail.properties.mail.smtp.auth"), "true"));
            emailConfig.setStarttls(Objects.equals(env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"), "true"));
            emailConfig.setUsername(env.getProperty("spring.mail.username"));
            emailConfig.setPassword(env.getProperty("spring.mail.password"));

            configuration = new SystemConfiguration();
            configuration.setConfigurationKey(EMAIL_CONFIG);
            configuration.setConfigurationName("Email Configuration");
            configuration.setDescription("Set up default email configuration");
            configuration.setValue(emailConfig.toJSONString());

            this.create(configuration);
        }
    }

    @Override
    public Collection<SystemConfigurationDto> fetchSystemConfigurations(String group) {
        Collection<SystemConfiguration> systemConfigurations = this.repository.findByConfigurationGroupOrderBySortOrderAsc(group);
        return this.convertModelsToDto(systemConfigurations);
    }

    @Override
    @Transactional
    public void updateSystemConfigurations(String group, Map<String, String> dto) {
        Collection<SystemConfiguration> systemConfigurations = this.repository.findByConfigurationGroupOrderBySortOrderAsc(group);
        Map<String, SystemConfiguration> mapByKey = this.mapConfigurationsByKey(systemConfigurations);
        dto.forEach((key, value) -> {
            if (mapByKey.containsKey(key)) {
                mapByKey.get(key).setValue(value);
            }
        });
    }

    private Map<String, SystemConfiguration> mapConfigurationsByKey(Collection<SystemConfiguration> systemConfigurations) {
        Map<String, SystemConfiguration> mapByKey = null;
        if (CollectionUtils.isNotEmpty(systemConfigurations)) {
            mapByKey = new HashMap<>();
            for (SystemConfiguration config : systemConfigurations) {
                mapByKey.put(config.getConfigurationKey(), config);
            }
        }
        return mapByKey == null ? Collections.emptyMap() : mapByKey;
    }

    private Collection<SystemConfigurationDto> convertModelsToDto(Collection<SystemConfiguration> systemConfigurations) {

        Collection<SystemConfigurationDto> dtos = new ArrayList<>();

        for (SystemConfiguration systemConfiguration : systemConfigurations) {
            SystemConfigurationDto dto = new SystemConfigurationDto();
            dto.setDescription(systemConfiguration.getDescription());
            dto.setKey(systemConfiguration.getConfigurationKey());
            dto.setName(systemConfiguration.getConfigurationName());
            dto.setValue(systemConfiguration.getValue());
            dto.setType(systemConfiguration.getSystemConfigrationType().name());
            ConfigurationMetadata metadata = new ConfigurationMetadata();
            dto.setMetadata(metadata);
            dtos.add(dto);
        }
        return dtos;
    }
}
