package com.indulgetech.controllers.user.admin;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.FileUrlDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.UserProfileDto;
import com.indulgetech.dto.user.admin.AdminUserDetailDto;
import com.indulgetech.exceptions.InvalidRequestException;
import com.indulgetech.security.admin.AdminUserInfo;
import com.indulgetech.services.user.admin.AdminUserService;
import com.indulgetech.utils.ApiResponseUtils;
import com.indulgetech.utils.FileUploadValidatorUtils;
import com.indulgetech.utils.UserInfoUtil;
import com.indulgetech.validators.ValidationErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-admin}/account")
public class AdminUserAccountController implements IAdminUserAccountController {

    public static final int MAX_PROFILE_PIC_SIZE = 1024 * 1024 * 2; //2mb
    private final UserInfoUtil userInfoUtil;
    private final AdminUserService adminUserService;

    @Override
    @GetMapping
    public ResponseEntity<ApiDataResponse<UserProfileDto>> fetchUserProfile() {

        AdminUserInfo userInfo = (AdminUserInfo) this.userInfoUtil.authenticatedUserInfo();
        UserProfileDto userProfileDto = this.adminUserService.fetchAdminUserProfile(userInfo.getUsername());
        return ApiResponseUtils.response(HttpStatus.OK, userProfileDto, "Resource retrieved successfully");
    }

    @Override
    @PostMapping("/changepassword")
    public ResponseEntity<ApiDataResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) throws Exception {

        validateSamePassword(changePasswordDto);

        AdminUserInfo userInfo = (AdminUserInfo) this.userInfoUtil.authenticatedUserInfo();
        this.adminUserService.changePassword(userInfo.getUsername(), changePasswordDto);
        return ApiResponseUtils.response(HttpStatus.OK, "Password changed successfully");
    }

    private void validateSamePassword(ChangePasswordDto changePasswordDto) {
        ValidationErrors validationErrors = new ValidationErrors();
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            validationErrors.addError("changePasswordDto", "newPassword", changePasswordDto.getNewPassword(), "New Password and confirmation password are not the same");
        }
        if (validationErrors.hasErrors()) {
            throw new InvalidRequestException(validationErrors);
        }
    }

    @Override
    @PutMapping
    public ResponseEntity<ApiDataResponse<AdminUserDetailDto>> updateProfile(
            @Valid @RequestBody UpdateUserProfileDto dto){
        AdminUserInfo userInfo = this.userInfoUtil.authenticatedAdminUserInfo();
        AdminUserDetailDto responseDto = this.adminUserService.updateProfile(userInfo.getUserId(),dto);
        return ApiResponseUtils.response(HttpStatus.OK, responseDto,"Resource updated successfully");
    }


    @Override
    @PutMapping("/profilePic")
    public ResponseEntity<ApiDataResponse<FileUrlDto>> updateProfilePic(@RequestPart(value = "profile_pic_upload") MultipartFile profileFileUpload) {
        validateProfilePicUpload(profileFileUpload);
        AdminUserInfo userInfo = this.userInfoUtil.authenticatedAdminUserInfo();
        return ApiResponseUtils.response(HttpStatus.OK,new FileUrlDto(this.adminUserService.updateProfilePic(userInfo.getUserId(),profileFileUpload)), "Resource updated successfully");
    }

    private void validateProfilePicUpload(MultipartFile profileFileUpload) {
        ValidationErrors validationErrors = new ValidationErrors();
        List<String> mimeTypes = List.of("jpg", "jpeg", "gif", "png");
        FileUploadValidatorUtils.validateRequired(profileFileUpload, validationErrors);
        FileUploadValidatorUtils.validateFileType(profileFileUpload, mimeTypes, validationErrors);
        FileUploadValidatorUtils.validateSize(profileFileUpload, MAX_PROFILE_PIC_SIZE, validationErrors);
        if (validationErrors.hasErrors()) {
            throw new InvalidRequestException(validationErrors);
        }
    }

}
