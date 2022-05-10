package com.indulgetech.services.user.admin;


import com.indulgetech.dto.user.role.ListRoleDto;
import com.indulgetech.dto.user.role.RoleDetailDto;
import com.indulgetech.dto.user.role.RoleRequestDto;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.services.generic.BaseEntityService;

import java.util.Collection;
import java.util.Optional;


public interface RoleService  extends BaseEntityService<Integer, Role> {
    Optional<Role> fetchByRoleKey(String roleKey);

    Role createRole(RoleRequestDto roleRequestDto);

    Collection<ListRoleDto> manageRoles();

    Collection<ListRoleDto> listRoles();

    Collection<ListRoleDto> listRolesForClient();

    Collection<ListRoleDto> listRolesForAdmin();

    RoleDetailDto fetchRoleDetail(Integer roleId);

    Role updateRole(Integer roleId, RoleRequestDto roleRequestDto);

    void deleteRole(Integer roleId);

    Collection<Role> getRoleByIds(Collection<Integer> ids);

    Collection<ListRoleDto> listRoles(String roleType);
}
