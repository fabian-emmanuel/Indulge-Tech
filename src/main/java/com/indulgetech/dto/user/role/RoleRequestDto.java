package com.indulgetech.dto.user.role;

import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.validators.enumvalidator.Enum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDto {
    private Collection<String> permissions;
    @Enum(enumClass= RoleType.class)
    private String roleType;
    @NotEmpty
    @Size(max=100)
    private String name;
    @Size(max=255)
    private String description;

}
