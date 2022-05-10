package com.indulgetech.dto.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UpdateUserProfileDto {
    @NotEmpty
    @Size(max = 100)
    private String firstName;
    @NotEmpty
    @Size(max = 100)
    private String lastName;
    @NotEmpty
    @Size(max = 100)
    private String phone;
}
