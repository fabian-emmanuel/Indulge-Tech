package com.indulgetech.dto.user.admin;

import com.indulgetech.dto.user.UserDto;
import lombok.Data;

import java.util.Collection;

@Data
//public class AdminUserRequestDto extends AdminUserDto {
public class AdminUserRequestDto extends UserDto {
    Collection<Integer> roles;
}
