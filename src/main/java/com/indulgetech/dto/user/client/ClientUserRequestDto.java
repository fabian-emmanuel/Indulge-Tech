package com.indulgetech.dto.user.client;

import com.indulgetech.dto.user.UserDto;
import com.indulgetech.validators.password.PasswordValueMatch;
import com.indulgetech.validators.password.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
@PasswordValueMatch.List(valid = {
        @PasswordValueMatch(
                field = "password",
                confirmField = "confirmPassword"
        )
})
public class ClientUserRequestDto extends UserDto {
    @ValidPassword
    @NotEmpty
    private String password;

    @ValidPassword
    @NotEmpty
    private String confirmPassword;

}
