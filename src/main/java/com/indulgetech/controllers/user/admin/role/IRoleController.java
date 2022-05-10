package com.indulgetech.controllers.user.admin.role;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.constants.Constants;
import com.indulgetech.dto.common.IdResponseDto;
import com.indulgetech.dto.user.role.ListRoleDto;
import com.indulgetech.dto.user.role.RoleDetailDto;
import com.indulgetech.dto.user.role.RoleRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Collection;

@Tag(name = "Role Controller")
public interface IRoleController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request!!!"),
            @ApiResponse(responseCode = "409", description = "non unique entity!!!"),
            @ApiResponse(responseCode = "401", description = "not authorized!")})
    @Operation(summary = "Create Role", description = "authorities[manageRole]")
    ResponseEntity<ApiDataResponse<IdResponseDto>> createRole(@Valid @RequestBody RoleRequestDto roleDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!")})
    @Operation(summary = "List/Manage All roles", description = "Use for listing all roles in the Admin role management module")
    ResponseEntity<ApiDataResponse<Collection<ListRoleDto>>> listAllRoles() throws Exception;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!")})
    @Operation(summary = "List roles",  description ="Use for populating drop downs and menu lists. List only admin type roles when type is "+ Constants.ROLE_TYPE_ADMIN +" " +
            "and only client type roles when type is "+ Constants.ROLE_TYPE_CLIENT)
    @Schema(name = "type", description = "Accepted values "+Constants.ROLE_TYPE_ADMIN+" | "+Constants.ROLE_TYPE_CLIENT, implementation = String.class, type = "query")
    ResponseEntity<ApiDataResponse<Collection<ListRoleDto>>> listRoles(@RequestParam(name = "type", required = false) String type) throws Exception;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Fetch role detail", description = "authorities[manageRole, viewAccessOnly]")
    ResponseEntity<ApiDataResponse<RoleDetailDto>> fetchRoleDetail(@PathVariable int roleId) throws Exception;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Update role", description = "authorities[manageRole]")
    ResponseEntity<ApiDataResponse<Object>> updateRole(
            @Valid @RequestBody RoleRequestDto dto, @PathVariable int roleId) throws Exception;

    ResponseEntity<ApiDataResponse<String>> deleteRole(@PathVariable int roleId) throws Exception;
}
