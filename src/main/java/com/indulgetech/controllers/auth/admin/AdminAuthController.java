package com.indulgetech.controllers.auth.admin;



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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basepath-admin}")
@Tag(name = "Admin Authentication Controller")
public class AdminAuthController {

    private final AuthService adminAuthService;
    private final AuthTokenProvider authTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "not authorized!")})
    @PostMapping("/login")
    public ResponseEntity<ApiDataResponse<LoginResponseDto>> login(@RequestBody LoginRequest loginRequest) {
        log.info(loginRequest.toString());
        LoginResponseDto loginResponseDto = this.adminAuthService.login(loginRequest.getUserName(),loginRequest.getPassword());

        return ApiResponseUtils.response(HttpStatus.OK, loginResponseDto, "Login successfully");
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "401", description = "not authorized!")})
    @PostMapping("/logout")
    public ResponseEntity<ApiDataResponse<Object>> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request) throws Exception {
        this.adminAuthService.logout(authTokenProvider.extractTokenFromRequest(request),logoutRequest);
        return ApiResponseUtils.response(HttpStatus.OK, "Logout successfully");
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "401", description = "not authorized!")})
    @PostMapping("/refreshToken")
    public ResponseEntity<ApiDataResponse<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = this.refreshTokenService.refreshAdminUserToken(refreshTokenRequest);
        return ApiResponseUtils.response(HttpStatus.OK, new RefreshTokenResponse(token));
    }
}
