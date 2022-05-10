package com.indulgetech.controllers.user.admin.permission;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.user.permission.PermissionDto;
import com.indulgetech.services.user.admin.PermissionService;
import com.indulgetech.utils.ApiResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-admin}/permissions")
@PreAuthorize("hasAuthority('manageRole')")
public class PermissionController implements IPermissionController {

    private final PermissionService permissionService;

    @Override
    @GetMapping
    public ResponseEntity<ApiDataResponse<Collection<PermissionDto>>> listPermissions() throws Exception {
        Collection<PermissionDto> list = this.permissionService.listPermissions();
        return ApiResponseUtils.response(HttpStatus.OK, list, "Resources retrieved successfully");
    }
}
