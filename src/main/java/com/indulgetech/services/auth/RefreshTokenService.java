package com.indulgetech.services.auth;


import com.indulgetech.dto.auth.RefreshTokenRequest;
import com.indulgetech.models.users.token.UserRefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    String refreshToken(RefreshTokenRequest refreshTokenRequest);

    UserRefreshToken save(UserRefreshToken refreshToken);

    Optional<UserRefreshToken> findByToken(String token);

    void deleteToken(UserRefreshToken refreshToken);

    String refreshAdminUserToken(RefreshTokenRequest refreshTokenRequest);

    String refreshClientToken(RefreshTokenRequest refreshTokenRequest);
}
