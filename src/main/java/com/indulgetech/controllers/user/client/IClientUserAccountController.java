package com.indulgetech.controllers.user.client;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.FileUrlDto;
import com.indulgetech.dto.common.IdResponseDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.client.ClientUserDetailDto;
import com.indulgetech.dto.user.client.ClientUserProfileDto;
import com.indulgetech.dto.user.client.ClientUserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Tag(name = "Client User Controller")
public interface IClientUserAccountController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request!!!"),
            @ApiResponse(responseCode = "409", description = "non unique entity!!!"),
            @ApiResponse(responseCode = "401", description = "not authorized!")})
    @Operation(summary = "Create Client user")
    ResponseEntity<ApiDataResponse<IdResponseDto>> createUser(@Valid @RequestBody ClientUserRequestDto clientUserRequestDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Fetch user profile")
    ResponseEntity<ApiDataResponse<ClientUserProfileDto>> fetchUserProfile();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request!!!"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Change Password")
    ResponseEntity<ApiDataResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) throws Exception;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Update profile(personal info) for current authenticated client user")
    ResponseEntity<ApiDataResponse<ClientUserDetailDto>> updateProfile(
            @Valid @RequestBody UpdateUserProfileDto dto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Update profile pic for current authenticated user")
    @Schema(name = "profile_pic_upload", description = "Accepted file types:png,jpg,jpeg max size:2mb", implementation = MultipartFile.class, type = "body")
    ResponseEntity<ApiDataResponse<FileUrlDto>> updateProfilePic(@RequestPart(value = "profile_pic_upload") MultipartFile profileFileUpload);
}
