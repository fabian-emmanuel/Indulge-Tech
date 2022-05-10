package com.indulgetech.dto.user.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.indulgetech.dto.common.ListingDto;
import com.indulgetech.models.users.roles.RoleType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListRoleDto extends ListingDto {

   private String description;
   private String roleType;
    @JsonProperty("isSystemCreated")
    private boolean isSystemCreated;

    public ListRoleDto(Integer id, String name, String description, RoleType roleType, boolean systemCreated) {
        super(id.longValue(), name);
        this.description=description;
        this.roleType=roleType!=null?roleType.name():"";
        this.isSystemCreated=systemCreated;
    }

}
