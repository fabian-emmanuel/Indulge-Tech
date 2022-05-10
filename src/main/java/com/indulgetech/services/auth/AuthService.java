package com.indulgetech.services.auth;

import com.indulgetech.dto.auth.LoginResponseDto;
import com.indulgetech.dto.auth.LogoutRequest;

public interface AuthService {
    LoginResponseDto login(String username, String password);
    void logout(String token, LogoutRequest logoutRequest);
}
