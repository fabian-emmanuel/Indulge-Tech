package com.indulgetech.dto.user.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    @JsonProperty(value = "id")
    public String permissionkey;
    @JsonProperty(value = "name")
    public String permissionTitle;
    public String description;

    public PermissionDto(String permissionKey, String permissionName) {
        this.permissionTitle=permissionName;
        this.permissionkey=permissionKey;
    }
}
