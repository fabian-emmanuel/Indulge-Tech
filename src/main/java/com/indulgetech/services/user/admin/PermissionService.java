package com.indulgetech.services.user.admin;

import com.indulgetech.dto.user.permission.PermissionDto;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.permissions.PermissionType;
import com.indulgetech.services.generic.BaseEntityService;

import java.util.Collection;
import java.util.Optional;

public interface PermissionService  extends BaseEntityService<Integer,Permission> {
    Optional<Permission> findPermByKey(String anyString);
    Collection<Permission> fetchPermissions();
    Optional<Permission> fetchByPermission(String permission);

    Collection<Permission> findByPermissionIn(Collection<String> names);

    Collection<Permission> fetchPermissions(PermissionType permissionType);

    Collection<PermissionDto> listPermissions();
}
