package com.indulgetech.utils;

import com.indulgetech.apiresponse.ApiDataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtils {

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, T data, String message ){
        return ApiResponseUtils.getResponse(status,data,message);
    }

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, T data){
        String message=null;
        if(status.equals(HttpStatus.OK)){
            message="Success";
        }
        return ApiResponseUtils.getResponse(status,data,message);
    }

    private static  <T> ResponseEntity<ApiDataResponse<T>> getResponse(HttpStatus status, T data, String message ){
        ApiDataResponse<T> ar = new ApiDataResponse<>(HttpStatus.OK);
        ar.setData(data);
        ar.setMessage(message);
        return new ResponseEntity<>(ar,status);
    }

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, String message ){
        return ApiResponseUtils.getResponse(status,null,message);
    }

    public static  ResponseEntity<ApiDataResponse> errorResponse(HttpStatus status, String errMsg, String debugMsg, String customErrCd ){
        return ApiResponseUtils.getErrResponse(status,errMsg,debugMsg,customErrCd);
    }

    private static ResponseEntity<ApiDataResponse> getErrResponse(HttpStatus status, String errMsg, String debugMsg, String customErrCd  ){
        ApiDataResponse ar = new ApiDataResponse<>(status);
        ar.setMessage(errMsg);
        ar.setErrorCode(customErrCd);
        ar.setDebugMessage(debugMsg);
        //ar.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(ar,status);
    }

    public static ResponseEntity<ApiDataResponse> errorResponse(HttpStatus status, String errMsg, String debugMsg ){
        return ApiResponseUtils.getErrResponse(status,errMsg,debugMsg,null);
    }

    public static ResponseEntity<ApiDataResponse> errorResponse(HttpStatus status, String errMsg){
        return ApiResponseUtils.getErrResponse(status,errMsg,null,null);
    }
}
