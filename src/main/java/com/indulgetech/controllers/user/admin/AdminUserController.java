package com.indulgetech.controllers.user.admin;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.IdResponseDto;
import com.indulgetech.dto.user.admin.AdminUserDetailResponseDto;
import com.indulgetech.dto.user.admin.AdminUserRequestDto;
import com.indulgetech.dto.user.admin.ListAdminUserDto;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.services.user.admin.AdminUserService;
import com.indulgetech.utils.ApiResponseUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-admin}")
public class AdminUserController implements IAdminUserController {

    private final AdminUserService adminUserService;

    @Override
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('manageAdminUser')")
    public ResponseEntity<ApiDataResponse<IdResponseDto>> createUser(@Valid @RequestBody AdminUserRequestDto adminUserRequestDto) {
        AdminUser adminUser = this.adminUserService.createUser(adminUserRequestDto);
        return ApiResponseUtils.response(HttpStatus.CREATED, new IdResponseDto(adminUser.getId()), "Resource created successfully");
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyAuthority('manageAdminUser', 'viewAccessOnly')")
    public ResponseEntity<ApiDataResponse<Page<ListAdminUserDto>>> listUsers(Pageable pageable){
        Page<ListAdminUserDto> list = this.adminUserService.listUsers(pageable);
        return ApiResponseUtils.response(HttpStatus.OK, list, "Resources retrieved successfully");
    }

    @Override
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('manageAdminUser', 'viewAccessOnly')")
    public ResponseEntity<ApiDataResponse<AdminUserDetailResponseDto>> fetchAdminUserDetail(@PathVariable Long userId)  {
        AdminUserDetailResponseDto userDetailResponseDto = this.adminUserService.fetchAdminUserDetail(userId);
        return ApiResponseUtils.response(HttpStatus.OK,userDetailResponseDto, "Resource retrieved successfully");
    }

    @Override
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('manageAdminUser')")
    public ResponseEntity<ApiDataResponse<Object>> updateAdminUser(
            @Valid @RequestBody AdminUserRequestDto dto,@PathVariable Long userId){
        AdminUser adminUser = this.adminUserService.updateAdminUser(userId,dto);
        return ApiResponseUtils.response(HttpStatus.OK, "Resource updated successfully");
    }

    @Override
    @PutMapping("{userId}/deactivate")
    @PreAuthorize("hasAuthority('manageAdminUser')")
    public ResponseEntity<ApiDataResponse<Object>> deactivateUserAccount(@PathVariable Long userId)  {
        this.adminUserService.deactivateUser(userId);
        return ApiResponseUtils.response(HttpStatus.OK,"Resource deactivated successfully");
    }

    @Override
    @PutMapping("{userId}/activate")
    @PreAuthorize("hasAuthority('manageAdminUser')")
    public ResponseEntity<ApiDataResponse<Object>> activateUserAccount(@PathVariable Long userId)  {
        this.adminUserService.activateUser(userId);
        return ApiResponseUtils.response(HttpStatus.OK,"Resource deactivated successfully");
    }

}
