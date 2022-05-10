package com.indulgetech.controllers.auth;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.auth.ForgotPasswordRequestDto;
import com.indulgetech.dto.auth.ResetPasswordDto;
import com.indulgetech.exceptions.InvalidRequestException;
import com.indulgetech.models.users.UserType;
import com.indulgetech.services.user.admin.AdminUserService;
import com.indulgetech.services.user.client.ClientUserService;
import com.indulgetech.utils.ApiResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath}")
public class PasswordResetController implements IPasswordResetController {

    private final AdminUserService adminUserService;
    private final ClientUserService organisationUserService;

    @Override
    @PostMapping("/forgotpassword/{userType}")
    public ResponseEntity<ApiDataResponse<Object>> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto, @PathVariable String userType) throws Exception {

        this.validateUserType(userType);
        //TODO: use strategy
        if(userType.equals(UserType.ADMIN.name().toLowerCase())){
            adminUserService.publishForgotPasswordEmail(forgotPasswordRequestDto.getEmail());
        }else if(userType.equals(UserType.CLIENT.name().toLowerCase())){
            organisationUserService.publishForgotPasswordEmail(forgotPasswordRequestDto.getEmail());
        }
        return ApiResponseUtils.response(HttpStatus.OK, "Forgot password initiated successfully");
    }


    @Override
    @PostMapping("/resetpassword/{userType}")
    public ResponseEntity<ApiDataResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto, @PathVariable String userType) throws Exception {

        this.validateUserType(userType);
        //TODO: use strategy
        if(userType.equals(UserType.ADMIN.name().toLowerCase())){
            adminUserService.resetPassword(resetPasswordDto);
        }else if(userType.equals(UserType.CLIENT.name().toLowerCase())){
            organisationUserService.resetPassword(resetPasswordDto);
        }
        //this.passwordResetService.resetPassword(userType,resetPasswordDto);
        return ApiResponseUtils.response(HttpStatus.OK, "Password changed successfully");
    }


    private void validateUserType(String userType) {
        if (!"admin".equals(userType) && !"client".equals(userType)) {
            throw new InvalidRequestException("Invalid usertype");
        }
    }


}
