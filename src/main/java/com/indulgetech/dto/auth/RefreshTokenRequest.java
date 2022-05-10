package com.indulgetech.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
    private String accessToken;//todo remove redundant
    private String userName;
}
