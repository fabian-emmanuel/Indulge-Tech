package com.indulgetech.controllers.user.admin.role;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.IdResponseDto;
import com.indulgetech.dto.user.role.ListRoleDto;
import com.indulgetech.dto.user.role.RoleDetailDto;
import com.indulgetech.dto.user.role.RoleRequestDto;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.services.user.admin.RoleService;
import com.indulgetech.utils.ApiResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath}/roles")
public class RoleController implements IRoleController {

    private final RoleService roleService;

    @Override
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('manageRole')")
    public ResponseEntity<ApiDataResponse<IdResponseDto>> createRole(@Valid @RequestBody RoleRequestDto roleDto) {
        Role role = roleService.createRole(roleDto);
        return ApiResponseUtils.response(HttpStatus.CREATED, new IdResponseDto(role.getId().longValue()), "Resource created successfully");
    }


    @Override
    @GetMapping("/manage")
    @PreAuthorize("hasAnyAuthority('manageRole', 'viewAccessOnly')")
    public ResponseEntity<ApiDataResponse<Collection<ListRoleDto>>> listAllRoles() throws Exception {
        Collection<ListRoleDto> list = this.roleService.manageRoles();
        return ApiResponseUtils.response(HttpStatus.OK, list, "Resources retrieved successfully");
    }


    @Override
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('manageRole', 'viewAccessOnly')")
    public ResponseEntity<ApiDataResponse<Collection<ListRoleDto>>> listRoles(@RequestParam(name = "type", required = false) String type) throws Exception {
        Collection<ListRoleDto> list = this.roleService.listRoles(type);
        return ApiResponseUtils.response(HttpStatus.OK, list, "Resources retrieved successfully");
    }

    @Override
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAnyAuthority('manageRole', 'viewAccessOnly')")
    public ResponseEntity<ApiDataResponse<RoleDetailDto>> fetchRoleDetail(@PathVariable int roleId) throws Exception {
        RoleDetailDto roleDetailDto = this.roleService.fetchRoleDetail(roleId);
        return ApiResponseUtils.response(HttpStatus.OK,roleDetailDto, "Resource retrieved successfully");
    }

    @Override
    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('manageRole')")
    public ResponseEntity<ApiDataResponse<Object>> updateRole(
            @Valid @RequestBody RoleRequestDto dto, @PathVariable int roleId) throws Exception {
        this.roleService.updateRole(roleId, dto);
        return ApiResponseUtils.response(HttpStatus.OK, "Resource updated successfully");
    }

    @Override
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('manageRole')")
    public ResponseEntity<ApiDataResponse<String>> deleteRole(@PathVariable int roleId) throws Exception {
        this.roleService.deleteRole(roleId);
        return ApiResponseUtils.response(HttpStatus.NO_CONTENT, "Resource deleted successfully");
    }
}
