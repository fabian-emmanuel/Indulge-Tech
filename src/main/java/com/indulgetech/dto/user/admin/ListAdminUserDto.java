package com.indulgetech.dto.user.admin;

import com.indulgetech.dto.user.ListUserDto;
import com.indulgetech.models.users.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ListAdminUserDto extends ListUserDto {

    public ListAdminUserDto(Long id, String name, String email, String phone, UserStatus status, Date dateAdded) {
        super(id, name,email,phone,status,dateAdded);
    }
}



