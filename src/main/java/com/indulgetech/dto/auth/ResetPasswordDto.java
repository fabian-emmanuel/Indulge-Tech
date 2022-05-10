package com.indulgetech.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String newPassword;
    private String confirmPassword;
    private String token;
}
