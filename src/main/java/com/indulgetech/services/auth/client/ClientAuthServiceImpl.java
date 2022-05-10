package com.indulgetech.services.auth.client;


import com.indulgetech.constants.PrcRsltCode;
import com.indulgetech.dto.auth.LoginResponseDto;
import com.indulgetech.dto.auth.LogoutRequest;
import com.indulgetech.exceptions.AccountDisabledException;
import com.indulgetech.exceptions.CustomException;
import com.indulgetech.exceptions.InvalidCredentialsException;
import com.indulgetech.models.users.UserType;
import com.indulgetech.models.users.client.ClientLoginHistory;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.models.users.token.CustomerRefreshToken;
import com.indulgetech.models.users.token.UserRefreshToken;
import com.indulgetech.repositories.user.ClientUserRepository;
import com.indulgetech.security.AuthTokenProvider;
import com.indulgetech.services.auth.AuthService;
import com.indulgetech.services.auth.RefreshTokenService;
import com.indulgetech.services.auth.TokenBlacklistService;
import com.indulgetech.utils.AuthUtils;
import com.indulgetech.utils.CodeGeneratorUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service("clientAuthService")
public class ClientAuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthServiceImpl.class);

    private final AuthTokenProvider tokenProvider;
    private final ClientUserRepository repository;
    private final ClientLoginHistoryService loginHistoryService;
    private final AuthenticationManager clientAuthenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponseDto login(String username, String password) {

        try {
            Authentication authentication = clientAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            ClientUser user = repository.findByEmail(username).orElseThrow(()->new InvalidCredentialsException("User with supplied credential does not exist"));

            String token = tokenProvider.generateToken(authentication.getName(), UserType.CLIENT.name());
            if (isEmptyToken(token)) {
                LOGGER.error("Unable to generate token");
                throw new CustomException.UnAuthorizeException("invalid username/password:");
            }

            CustomerRefreshToken refreshToken = this.createRefreshTokenModel(user);
            refreshToken = (CustomerRefreshToken) this.saveRefreshToken(refreshToken);

            //create login history
            this.createLoginHistory(user, PrcRsltCode.SUCCESS, "");

            //create response
            LoginResponseDto authResponse = this.createLoginResponse(user, token);
//            authResponse.setUser(user);
            authResponse.setAuthorities(AuthUtils.buildAuthorities(authentication));
            authResponse.setRefreshToken(refreshToken.getToken());
            return authResponse;

        } catch (Exception e) {
            this.repository.findByEmail(username).ifPresent(user -> {
                this.createLoginHistory(user, PrcRsltCode.FAILURE, "");
            });
            if (e instanceof BadCredentialsException) {
                throw new InvalidCredentialsException("invalid username/password:");
            } else if (e instanceof DisabledException) {
                throw new AccountDisabledException("Account disabled");
            } else {
                throw e;
            }
        }
    }

    private UserRefreshToken saveRefreshToken(CustomerRefreshToken refreshToken) {
        return this.refreshTokenService.save(refreshToken);
    }

    private CustomerRefreshToken createRefreshTokenModel(ClientUser user) {
        CustomerRefreshToken refreshToken = new CustomerRefreshToken();
        //refreshToken.setClientUser(user);
        refreshToken.setUserName(user.getEmail());
        int validityTrm = 10080;//mins(7days) todo:should come from configuration
        refreshToken.calculateExpiryDate(String.valueOf(validityTrm));
        refreshToken.setToken(CodeGeneratorUtils.generateRefreshToken());
        return refreshToken;
    }


    @Override
    @Transactional
    public void logout(String token, LogoutRequest logoutRequest) {
        Optional<UserRefreshToken> refreshToken = this.refreshTokenService.findByToken(logoutRequest.getRefreshToken());
        refreshToken.ifPresent(this.refreshTokenService::deleteToken);
        this.tokenBlacklistService.blacklistToken(token);
    }

    private boolean isEmptyToken(String token) {
        return AuthUtils.isEmptyToken(token);
    }

    private LoginResponseDto createLoginResponse(ClientUser user, String token) {
        return AuthUtils.createLoginResponse(user, token);
    }

    private void createLoginHistory(ClientUser user, String prsRslt, String ip) {
        ClientLoginHistory loginHistory = new ClientLoginHistory();
        loginHistory.setUser(user);
        loginHistory.setPrcRslt(prsRslt);
        loginHistory.setIpAddr(ip);
        this.loginHistoryService.saveLoginHistory(loginHistory);
    }
}
