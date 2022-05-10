package com.indulgetech.services.auth;


import com.indulgetech.dto.auth.RefreshTokenRequest;
import com.indulgetech.exceptions.InvalidRefreshTokenException;
import com.indulgetech.models.users.UserType;
import com.indulgetech.models.users.token.CustomerRefreshToken;
import com.indulgetech.models.users.token.UserRefreshToken;
import com.indulgetech.repositories.token.CustomerRefreshTokenRepository;
import com.indulgetech.security.AuthTokenProvider;
import com.indulgetech.security.admin.AdminUserDetailsService;
import com.indulgetech.security.admin.AdminUserInfo;
import com.indulgetech.security.client.ClientUserDetailsService;
import com.indulgetech.security.client.ClientUserInfo;
import com.indulgetech.utils.CustomDateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final CustomerRefreshTokenRepository refreshTokenRepository;
    private final AuthTokenProvider authTokenProvider;
    private final AdminUserDetailsService adminUserDetailsService;
    private final ClientUserDetailsService clientUserDetailsService;

    @Override
    public String refreshToken(RefreshTokenRequest refreshTokenRequest) {
        this.validateToken(refreshTokenRequest);
        return authTokenProvider.generateToken(refreshTokenRequest.getUserName(), UserType.CLIENT.name());
    }


    private void validateToken(RefreshTokenRequest refreshTokenRequest) {
        Optional<UserRefreshToken> optional = this.refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken());

        if (optional.isEmpty()) {
            throw new InvalidRefreshTokenException("Token does not exist");
        }

        if (expiredToken(optional.get())) {
            throw new InvalidRefreshTokenException("Token has expired");
        }

        if (invalidTokenIssued(optional.get(), refreshTokenRequest.getUserName())) {
            throw new InvalidRefreshTokenException("Invalid Token issued");
        }
    }

    private boolean invalidTokenIssued(UserRefreshToken refreshToken, String requestUserName) {
        return !StringUtils.equals(refreshToken.getUserName(), requestUserName);
    }

    @Override
    public UserRefreshToken save(UserRefreshToken refreshToken) {
        return this.refreshTokenRepository.save((CustomerRefreshToken) refreshToken);
    }

    @Override
    public Optional<UserRefreshToken> findByToken(String token) {
        return this.refreshTokenRepository.findByToken(token);
    }

    @Override
    public void deleteToken(UserRefreshToken refreshToken) {
        this.refreshTokenRepository.delete((CustomerRefreshToken) refreshToken);
    }

    @Override
    public String refreshAdminUserToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            this.validateToken(refreshTokenRequest);
            AdminUserInfo userDetails = (AdminUserInfo) this.adminUserDetailsService.loadUserByUsername(refreshTokenRequest.getUserName());
            return authTokenProvider.generateToken(refreshTokenRequest.getUserName(), UserType.ADMIN.name());
        } catch (UsernameNotFoundException e) {
            throw new InvalidRefreshTokenException("Invalid username");
        }
    }

    @Override
    public String refreshClientToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            this.validateToken(refreshTokenRequest);
            ClientUserInfo userDetails = (ClientUserInfo) this.clientUserDetailsService.loadUserByUsername(refreshTokenRequest.getUserName());
            return authTokenProvider.generateToken(refreshTokenRequest.getUserName(), UserType.CLIENT.name());
        } catch (UsernameNotFoundException e) {
            throw new InvalidRefreshTokenException("Invalid username");
        }
    }

    private boolean expiredToken(UserRefreshToken customerRefreshToken) {
        Date now = CustomDateUtils.now();
        return customerRefreshToken.getValidityTrm().before(now);
    }
}
