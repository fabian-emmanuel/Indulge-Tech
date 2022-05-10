package com.indulgetech.controllers.auth.client;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.auth.*;
import com.indulgetech.security.AuthTokenProvider;
import com.indulgetech.services.auth.AuthService;
import com.indulgetech.services.auth.RefreshTokenService;
import com.indulgetech.utils.ApiResponseUtils;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-client}/")
@Tag(name =  "Client Authentication Controller")
public class ClientAuthController {

    private final AuthService clientAuthService;
    private final AuthTokenProvider authTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    @ApiResponses(value = {@ApiResponse(responseCode = "401", description = "not authorized!")})
    public ResponseEntity<ApiDataResponse<LoginResponseDto>> login(@RequestBody LoginRequest loginRequest) throws Exception {
        LoginResponseDto loginResponseDto = this.clientAuthService.login(loginRequest.getUserName(),loginRequest.getPassword());
        return ApiResponseUtils.response(HttpStatus.OK, loginResponseDto, "Login successfully");
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "401", description = "not authorized!")})
    @PostMapping("/logout")
    public ResponseEntity<ApiDataResponse<Object>> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request) throws Exception {
        this.clientAuthService.logout(authTokenProvider.extractTokenFromRequest(request),logoutRequest);
        return ApiResponseUtils.response(HttpStatus.OK, "Logout successfully");

    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ApiDataResponse<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = this.refreshTokenService.refreshClientToken(refreshTokenRequest);
        return ApiResponseUtils.response(HttpStatus.OK, new RefreshTokenResponse(token));
    }
}
