package com.indulgetech.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ChangePasswordDto {

    private String password;
    @Size(min =6)
    private String newPassword;
    private String confirmPassword;
}
