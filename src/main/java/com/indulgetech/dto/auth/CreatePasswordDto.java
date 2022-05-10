package com.indulgetech.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreatePasswordDto {
    private String token;
    @NotBlank(message = "message should not blank")
    @Size(max=64)
    private String password;
    private String confirmPassword;
}
