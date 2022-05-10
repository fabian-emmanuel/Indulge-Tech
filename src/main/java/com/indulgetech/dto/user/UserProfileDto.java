package com.indulgetech.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto extends UserDto{
    private Long userId;
    private String profilePic;
}
