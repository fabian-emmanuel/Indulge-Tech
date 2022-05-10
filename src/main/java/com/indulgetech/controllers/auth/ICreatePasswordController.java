package com.indulgetech.controllers.auth;


import com.indulgetech.apiresponse.ApiDataResponse;
import com.indulgetech.dto.auth.CreatePasswordDto;
import com.indulgetech.dto.auth.VerifyTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Tag(name = "Create Password Controller")
public interface ICreatePasswordController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid token!!!")})
    @Operation(summary = "Verify token" )
    ResponseEntity<ApiDataResponse<Object>> verifyToken(@Valid @RequestBody VerifyTokenDto verifyTokenDto) throws Exception;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid token!!!")})
    @Operation(summary = "Create password" )
    ResponseEntity<ApiDataResponse<Object>> createPassword(@Valid @RequestBody CreatePasswordDto createPasswordDto) throws Exception;
}
