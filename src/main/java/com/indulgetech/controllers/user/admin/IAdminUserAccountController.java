package com.indulgetech.controllers.user.admin;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.FileUrlDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.UserProfileDto;
import com.indulgetech.dto.user.admin.AdminUserDetailDto;
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

@Tag(name ="Admin User Account Controller")
public interface IAdminUserAccountController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "403", description = "forbidden!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Fetch user profile")
    ResponseEntity<ApiDataResponse<UserProfileDto>> fetchUserProfile();

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
    @Operation(summary = "Update profile(personal info) for current authenticated user")
    ResponseEntity<ApiDataResponse<AdminUserDetailDto>> updateProfile(
            @Valid @RequestBody UpdateUserProfileDto dto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "401", description = "not authorized!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Update profile pic for current authenticated user")
    @Schema(name = "profile_pic_upload", description = "Accepted file types:png,jpg,jpeg max size:2mb", implementation = MultipartFile.class, type = "body")
    ResponseEntity<ApiDataResponse<FileUrlDto>> updateProfilePic(@RequestPart(value = "profile_pic_upload") MultipartFile profileFileUpload);
}
