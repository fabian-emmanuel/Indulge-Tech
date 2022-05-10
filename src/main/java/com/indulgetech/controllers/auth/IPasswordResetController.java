package com.indulgetech.controllers.auth;

import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.auth.ForgotPasswordRequestDto;
import com.indulgetech.dto.auth.ResetPasswordDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Tag(name = "Password Reset Controller")
public interface IPasswordResetController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid request!!!"),
            @ApiResponse(responseCode = "404", description = "not found!!!")})
    @Operation(summary = "Publish forgot password email" )
    ResponseEntity<ApiDataResponse<Object>> initiateForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto, @PathVariable String userType) throws Exception;

    ResponseEntity<ApiDataResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto, @PathVariable String userType) throws Exception;
}
