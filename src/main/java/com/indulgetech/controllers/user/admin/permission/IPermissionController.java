package com.indulgetech.controllers.user.admin.permission;

import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.user.permission.PermissionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Tag(name ="Permission Controller")
public interface IPermissionController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!")})
    @Operation(summary = "List permissions",description = "authorities[manageRole]")
    ResponseEntity<ApiDataResponse<Collection<PermissionDto>>> listPermissions() throws Exception;
}
