package com.indulgetech.dto.user.client;

import lombok.Data;

@Data
public class ClientUserDetailDto extends ClientUserRequestDto{
    private long userId;
    private long organizationId;
    /*private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String status;*/
}



