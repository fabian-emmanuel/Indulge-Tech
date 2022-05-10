package com.indulgetech.dto.user.client;

import com.indulgetech.dto.user.UserProfileDto;
import lombok.Data;

@Data
public class ClientUserProfileDto extends UserProfileDto {

    private String role;
}
