package com.indulgetech.unit.service.system.email;


import com.indulgetech.AbstractTest;
import com.indulgetech.services.system.SystemConfigurationService;
import com.indulgetech.services.system.mail.EmailService;
import com.indulgetech.services.system.mail.templateEngine.TemplateEngine;
import com.indulgetech.utils.EmailTemplateUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@ExtendWith(MockitoExtension.class)
public class EmailTemplateUtilsTest extends AbstractTest {

    /**
     * System under test (SUT)
     */
    @InjectMocks
    EmailTemplateUtils emailTemplateUtils;

    @Mock
    EmailService emailService;

    @Mock
    SystemConfigurationService systemConfigurationService;

    @Mock
    TemplateEngine templateEngine;

    @Value("${api.url-domain}")
    private String urlDomain;

  /*  @Test
    void shouldSendCreatePasswordUrlLinkEmailSuccessfully() throws Exception {
        //arrange
        String body="emailBody";
        //act
        //input
        Organization organization = TestModels.organisation("Keystone", "KS");
        organization.setId(1L);
        OrganizationUser user = TestModels.organizationUser("olalekan", "paul", "", "client1@gmail.com",
                "", "08137640746");
        user.setOrganizationRole("Secretary");
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordResetToken(RandomStringUtils.randomAlphanumeric(32));
        user.setId(1L);
        String passwordUrl=urlDomain+ Constants.PASSWORD_URL+"?"+
                Constants.PASSWORD_URL_TOKEN_PARAM+user.getPasswordResetToken();

        when(this.templateEngine.processTemplateIntoString(anyString(),anyMap())).thenReturn(body);
        when(this.systemConfigurationService.findConfigValueByKey(anyString())).thenAnswer((Answer<String>) i -> {
            if(i.getArgument(0).equals(SchemaConstant.SUPPORT_EMAIL)){
              return "valid_email@decagonhq.com";
            }else{
              return RandomStringUtils.randomAlphabetic(10);
            }
        });
        doNothing().when(this.emailService).sendAsyncEmail(any(Email.class));
        this.emailTemplateUtils.sendCreatePasswordUrlLinkEmail(user,passwordUrl);
        //assert
        ArgumentCaptor<Email> attributeCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendAsyncEmail(attributeCaptor.capture());
        Email email=attributeCaptor.getValue();
        assertNotNull(email.getFrom());
        assertEquals("valid_email@decagonhq.com",email.getFromEmail());
        assertNotNull(email.getSubject());
        assertEquals(user.getEmail(),email.getTo());
        assertNotNull(email.getBody());
       // assertEquals(EmailConstants.EMAIL_CREATE_PASSWORD_TMPL,email.getTemplateName());
       // assertTrue(email.getTemplateTokens().containsKey(Constants.CREATE_PASSWORD_URL));
        // assertEquals(passwordUrl,email.getTemplateTokens().get(Constants.CREATE_PASSWORD_URL));
    }


    @Test
    void shouldNotSendCreatePasswordUrlLinkEmailWhenUnableToProcessEmailTemplate() throws Exception {
        //act
        //input
        Organization organization = TestModels.organisation("Keystone", "KS");
        organization.setId(1L);
        OrganizationUser user = TestModels.organizationUser("olalekan", "paul", "", "client1@gmail.com",
                "", "08137640746");
        user.setOrganizationRole("Secretary");
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordResetToken(RandomStringUtils.randomAlphanumeric(32));
        user.setId(1L);
        String passwordUrl=urlDomain+ Constants.PASSWORD_URL+"?"+
                Constants.PASSWORD_URL_TOKEN_PARAM+user.getPasswordResetToken();
        when(this.templateEngine.processTemplateIntoString(anyString(),anyMap())).thenThrow(new TemplateEngineException(""));
        //doThrow(new TemplateEngineException("")).when(this.templateEngine)

        assertThrows(TemplateEngineException.class, () -> {
            this.emailTemplateUtils.sendCreatePasswordUrlLinkEmail(user,passwordUrl);
        });
        //assert
        verify(this.emailService,never()).sendAsyncEmail(any(Email.class));
    }


    @Test
    void shouldUseSystemDefinedFromEmailWhenSendCreatePasswordUrlLinkEmailWithInvalidUserDefinedFromEmail() throws Exception {
        //act
        //input
        Organization organization = TestModels.organisation("Keystone", "KS");
        organization.setId(1L);
        OrganizationUser user = TestModels.organizationUser("olalekan", "paul", "", "client1@gmail.com",
                "", "08137640746");
        user.setOrganizationRole("Secretary");
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordResetToken(RandomStringUtils.randomAlphanumeric(32));
        user.setId(1L);
        String passwordUrl=urlDomain+ Constants.PASSWORD_URL+"?"+
                Constants.PASSWORD_URL_TOKEN_PARAM+user.getPasswordResetToken();

        when(this.systemConfigurationService.findConfigValueByKey(anyString())).thenAnswer((Answer<String>) i -> {
            if(i.getArgument(0).equals(SchemaConstant.SUPPORT_EMAIL)){
                return "";
            }else{
                return RandomStringUtils.randomAlphabetic(10);
            }
        });
        doNothing().when(this.emailService).sendAsyncEmail(any(Email.class));
        this.emailTemplateUtils.sendCreatePasswordUrlLinkEmail(user,passwordUrl);
        //assert
        ArgumentCaptor<Email> attributeCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendAsyncEmail(attributeCaptor.capture());
        Email email=attributeCaptor.getValue();
        assertEquals(SchemaConstant.DEFAULT_SUPPORT_EMAIL,email.getFromEmail());;
    }

    @Test
    void shouldSendPasswordCreatedEmailSuccessfully() throws Exception {
        //arrange
        String body="emailBody";
        //act
        //input
        Organization organization = TestModels.organisation("Keystone", "KS");
        organization.setId(1L);
        OrganizationUser user = TestModels.organizationUser("olalekan", "paul", "", "client1@gmail.com",
                "", "08137640746");
        user.setOrganizationRole("Secretary");
        user.setStatus(UserStatus.ACTIVE);
        user.setPasswordResetToken(RandomStringUtils.randomAlphanumeric(32));
        user.setId(1L);
        String clientUrl=urlDomain+"/"+ Constants.CLIENT_URI;

        when(this.templateEngine.processTemplateIntoString(anyString(),anyMap())).thenReturn(body);
        when(this.systemConfigurationService.findConfigValueByKey(anyString())).thenAnswer((Answer<String>) i -> {
            if(i.getArgument(0).equals(SchemaConstant.SUPPORT_EMAIL)){
                return "valid_email@decagonhq.com";
            }else{
                return RandomStringUtils.randomAlphabetic(10);
            }
        });
        doNothing().when(this.emailService).sendAsyncEmail(any(Email.class));
        this.emailTemplateUtils.sendClientPasswordCreatedEmail(user,clientUrl);
        //assert
        ArgumentCaptor<Email> attributeCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendAsyncEmail(attributeCaptor.capture());
        Email email=attributeCaptor.getValue();
        assertNotNull(email.getFrom());
        assertEquals("valid_email@decagonhq.com",email.getFromEmail());
        assertNotNull(email.getSubject());
        assertEquals(user.getEmail(),email.getTo());
        assertNotNull(email.getBody());
    }

    @Test
    void shouldSendCreateAdminUserEmailSuccessfully() throws Exception {
        //arrange
        String body="emailBody";
        //act
        //input
        AdminUser adminUser = TestModels.adminUser("olalekan", "peter", "", "admin@gmail.com",
                "admin", "08137640746");
        when(this.templateEngine.processTemplateIntoString(anyString(),anyMap())).thenReturn(body);
        when(this.systemConfigurationService.findConfigValueByKey(anyString())).thenAnswer((Answer<String>) i -> {
            if(i.getArgument(0).equals(SchemaConstant.SUPPORT_EMAIL)){
                return "valid_email@decagonhq.com";
            }else{
                return RandomStringUtils.randomAlphabetic(10);
            }
        });
        String plainPassword=RandomStringUtils.randomAlphabetic(10);
        doNothing().when(this.emailService).sendAsyncEmail(any(Email.class));
        this.emailTemplateUtils.sendCreateAdminUserEmail(adminUser,plainPassword);
        //assert
        ArgumentCaptor<Email> attributeCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendAsyncEmail(attributeCaptor.capture());
        Email email=attributeCaptor.getValue();
        assertNotNull(email.getFrom());
        assertEquals("valid_email@decagonhq.com",email.getFromEmail());
        assertNotNull(email.getSubject());
        assertEquals(adminUser.getEmail(),email.getTo());
        assertNotNull(email.getBody());
    }
*/

}
