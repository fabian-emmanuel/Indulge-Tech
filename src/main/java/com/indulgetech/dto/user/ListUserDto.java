package com.indulgetech.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.indulgetech.dto.common.ListingDto;
import com.indulgetech.models.users.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.indulgetech.constants.DateDisplayConstants.DATE_TIME_DISPLAY_FORMAT;

@Data
@NoArgsConstructor
public class ListUserDto extends ListingDto {

    private String status;
    @JsonFormat(pattern = DATE_TIME_DISPLAY_FORMAT)
    private Date dateAdded;
    private String name;
    private String email;
    private String phone;

    public ListUserDto(Long id, String name, String email, String phone, UserStatus status, Date dateAdded) {
        super(id, name);
        this.status = status.name();
        this.dateAdded = dateAdded;
        this.email=email;
        this.name=name;
        this.phone=phone;
    }
}



