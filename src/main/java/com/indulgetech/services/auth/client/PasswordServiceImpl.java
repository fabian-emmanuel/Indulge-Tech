package com.indulgetech.services.auth.client;


import com.indulgetech.constants.Constants;
import com.indulgetech.dto.auth.CreatePasswordDto;
import com.indulgetech.exceptions.InvalidRequestException;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.services.user.client.ClientUserService;
import com.indulgetech.utils.EmailTemplateUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PasswordServiceImpl implements PasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordServiceImpl.class);

    private final ClientUserService organisationUserService;
    private final PasswordEncoder passwordEncoder;
    private final EmailTemplateUtils emailTemplateUtils;

    @Value("${api.url-domain}")
    private String urlDomain;

    @Override
    @Transactional
    public boolean createOrganisationUserPassword(CreatePasswordDto createPasswordDto) {

        Optional<ClientUser> optional= this.organisationUserService.findByPasswordResetToken(createPasswordDto.getToken());
        if(optional.isEmpty()){
            throw new InvalidRequestException("invalid token: Token does not exist");
        }

        if(optional.get().tokenExpired()){
            throw new InvalidRequestException("invalid token: Token expired");
        }

        ClientUser user=optional.get();
        String encodedPassword = passwordEncoder.encode(createPasswordDto.getPassword());
        user.setPassword(encodedPassword);
        user.setPasswordResetToken(null);
        user.setPasswordResetValidityTerm(null);
        this.sendPasswordCreatedMail(user);
        return true;
    }

    @Override
    public boolean verifyToken(String token) {
        Optional<ClientUser> optional= this.organisationUserService.findByPasswordResetToken(token);
        if(optional.isEmpty()){
            throw new InvalidRequestException("invalid token: Token does not exist");
        }
        if(optional.get().tokenExpired()){
            throw new InvalidRequestException("invalid token: Token expired");
        }
        return true;
    }


    private void sendPasswordCreatedMail(ClientUser clientUser) {

        try {

            this.emailTemplateUtils.sendClientPasswordCreatedEmail(clientUser,this.createClientUrl() );

        } catch (Exception e) {
            LOGGER.error("Cannot send email to customer ",e);
        }
    }

    private String createClientUrl() {
        return urlDomain+ Constants.CLIENT_URI;
    }
}
