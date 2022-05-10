package com.indulgetech.controllers.user.client;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.common.FileUrlDto;
import com.indulgetech.dto.common.IdResponseDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.client.ClientUserDetailDto;
import com.indulgetech.dto.user.client.ClientUserProfileDto;
import com.indulgetech.dto.user.client.ClientUserRequestDto;
import com.indulgetech.exceptions.InvalidRequestException;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.security.client.ClientUserInfo;
import com.indulgetech.services.user.client.ClientUserService;
import com.indulgetech.utils.ApiResponseUtils;
import com.indulgetech.utils.FileUploadValidatorUtils;
import com.indulgetech.utils.UserInfoUtil;
import com.indulgetech.validators.ValidationErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${api.basepath-client}")
public class ClientUserAccountController implements IClientUserAccountController {

    public static final int MAX_PROFILE_PIC_SIZE = 1024 * 1024 * 2; //2mb
    private final UserInfoUtil userInfoUtil;
    private final ClientUserService clientUserService;


    @Override
    @PostMapping("/sign-up")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<ApiDataResponse<IdResponseDto>> createUser(@Valid @RequestBody ClientUserRequestDto clientUserRequestDto) {
        log.info("@@@@@@@@@@@@@@ Controller got called");
        ClientUser clientUser = this.clientUserService.createClientUser(clientUserRequestDto);
        return ApiResponseUtils.response(HttpStatus.CREATED, new IdResponseDto(clientUser.getId()), "Resource created successfully");
    }


    @Override
    @GetMapping
    public ResponseEntity<ApiDataResponse<ClientUserProfileDto>> fetchUserProfile() {
        ClientUserInfo userInfo = (ClientUserInfo) this.userInfoUtil.authenticatedUserInfo();
        ClientUserProfileDto userProfileDto = this.clientUserService.fetchClientUserProfile(userInfo.getUsername());
        return ApiResponseUtils.response(HttpStatus.OK, userProfileDto, "Resource retrieved successfully");
    }

    @Override
    @PostMapping("/changepassword")
    public ResponseEntity<ApiDataResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) throws Exception {

        validateSamePassword(changePasswordDto);

        ClientUserInfo userInfo = (ClientUserInfo) this.userInfoUtil.authenticatedUserInfo();
        this.clientUserService.changePassword(userInfo.getUsername(), changePasswordDto);
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
    public ResponseEntity<ApiDataResponse<ClientUserDetailDto>> updateProfile(
            @Valid @RequestBody UpdateUserProfileDto dto){
        ClientUserInfo userInfo = (ClientUserInfo) this.userInfoUtil.authenticatedUserInfo();
        ClientUserDetailDto responseDto = this.clientUserService.updateProfile(userInfo.getUser().getId(),dto);
        return ApiResponseUtils.response(HttpStatus.OK, responseDto,"Resource updated successfully");
    }

    @Override
    @PutMapping("/profilePic")
    public ResponseEntity<ApiDataResponse<FileUrlDto>> updateProfilePic(@RequestPart(value = "profile_pic_upload") MultipartFile profileFileUpload) {
        validateProfilePicUpload(profileFileUpload);
        ClientUserInfo userInfo = (ClientUserInfo) this.userInfoUtil.authenticatedUserInfo();
        return ApiResponseUtils.response(HttpStatus.OK,new FileUrlDto(this.clientUserService.updateProfilePic(userInfo.getUser().getId(),profileFileUpload)), "Resource updated successfully");
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
