package com.indulgetech.controllers.auth;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.auth.CreatePasswordDto;
import com.indulgetech.dto.auth.VerifyTokenDto;
import com.indulgetech.services.auth.client.PasswordService;
import com.indulgetech.utils.ApiResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-client}/createpassword")
public class CreatePasswordController implements ICreatePasswordController {

    private final PasswordService passwordService;

    @Override
    @PostMapping("/verify_token")
    public ResponseEntity<ApiDataResponse<Object>> verifyToken(@Valid @RequestBody VerifyTokenDto verifyTokenDto) throws Exception {
        this.passwordService.verifyToken(verifyTokenDto.getToken());
        return ApiResponseUtils.response(HttpStatus.OK, "Token verified successfully");
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiDataResponse<Object>> createPassword(@Valid @RequestBody CreatePasswordDto createPasswordDto) throws Exception {
        this.passwordService.createOrganisationUserPassword(createPasswordDto);
        return ApiResponseUtils.response(HttpStatus.OK, "Password created successfully");
    }


}
