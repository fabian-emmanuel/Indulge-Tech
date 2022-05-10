package com.indulgetech.dto.user.admin;

import com.indulgetech.dto.user.UserDto;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class AdminUserDto extends UserDto {

    @Size(min = 6,max=100)
    protected String password;
}
