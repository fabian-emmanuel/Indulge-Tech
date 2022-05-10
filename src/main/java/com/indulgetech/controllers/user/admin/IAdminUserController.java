package com.indulgetech.controllers.user.admin;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.IdResponseDto;
import com.indulgetech.dto.user.admin.AdminUserDetailResponseDto;
import com.indulgetech.dto.user.admin.AdminUserRequestDto;
import com.indulgetech.dto.user.admin.ListAdminUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Tag(name = "Admin User Controller")
public interface IAdminUserController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request!!!"),
            @ApiResponse(responseCode = "409", description = "non unique entity!!!"),
            @ApiResponse(responseCode = "401", description = "not authorized!")})
    @Operation(summary = "Create user", description = "authorities[manageAdminUser]")
    ResponseEntity<ApiDataResponse<IdResponseDto>> createUser(@Valid @RequestBody AdminUserRequestDto adminUserRequestDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!")})
    @Operation(summary = "List users", description = "authorities[manageAdminUser, viewAccessOnly]")
    ResponseEntity<ApiDataResponse<Page<ListAdminUserDto>>> listUsers(Pageable pageable);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Fetch admin user detail", description = "authorities[manageAdminUser, viewAccessOnly]")
    ResponseEntity<ApiDataResponse<AdminUserDetailResponseDto>> fetchAdminUserDetail(@PathVariable Long userId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Update admin user", description = "authorities[manageAdminUser]")
    ResponseEntity<ApiDataResponse<Object>> updateAdminUser(
            @Valid @RequestBody AdminUserRequestDto dto, @PathVariable Long userId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Deactivate user account", description = "authorities[manageAdminUser]")
    ResponseEntity<ApiDataResponse<Object>> deactivateUserAccount(@PathVariable Long userId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Activate user account", description = "authorities[manageAdminUser]")
    ResponseEntity<ApiDataResponse<Object>> activateUserAccount(@PathVariable Long userId);

}
