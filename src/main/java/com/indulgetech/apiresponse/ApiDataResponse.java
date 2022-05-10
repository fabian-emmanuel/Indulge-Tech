package com.indulgetech.apiresponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ApiDataResponse
 */
@Getter
@Setter
public class ApiDataResponse<T> {

  private HttpStatus status;
  private String message;
  private String errorCode;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private String debugMessage;
  private List<ApiSubError> subErrors;
  private T data;

  private ApiDataResponse() {
    timestamp = LocalDateTime.now();
  }

  public ApiDataResponse(HttpStatus status) {
    this();
    this.status = status;
  }

  public ApiDataResponse(HttpStatus status, Throwable ex) {
    this();
    this.status = status;
    this.errorCode = "Unexpected error";
    this.debugMessage = ex.getLocalizedMessage();
  }

  public ApiDataResponse(HttpStatus status, String message, Throwable ex) {
    this();
    this.status = status;
    this.message = message;
    this.debugMessage = ex.getLocalizedMessage();
  }

  private void addSubError(ApiSubError subError) {
    if (subErrors == null) {
      subErrors = new ArrayList<>();
    }
    subErrors.add(subError);
  }

  private void addValidationError(String object, String field, Object rejectedValue, String message) {
    addSubError(new ApiValidationError(object, field, rejectedValue, message));
  }

  private void addValidationError(String object, String message) {
    addSubError(new ApiValidationError(object, message));
  }

  private void addValidationError(FieldError fieldError) {
    this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(),
        fieldError.getDefaultMessage());
  }

  public void addValidationErrors(List<FieldError> fieldErrors) {
    fieldErrors.forEach(this::addValidationError);
  }

  private void addValidationError(ObjectError objectError) {
    //this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
    this.addValidationError(objectError.getObjectName(),objectError.getCode(),null, objectError.getDefaultMessage());
  }

  public void addValidationError(List<ObjectError> globalErrors) {
    globalErrors.forEach(this::addValidationError);
  }

  /**
   * Utility method for adding error of ConstraintViolation. Usually when
   * a @Validated validation fails.
   *
   * @param cv the ConstraintViolation
   */
  private void addValidationError(ConstraintViolation<?> cv) {
    this.addValidationError(cv.getRootBeanClass().getSimpleName(),
       ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), cv.getInvalidValue(), cv.getMessage());
  }

  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
    constraintViolations.forEach(this::addValidationError);
  }

  public void addValidationErrorsNew(Set<? extends ConstraintViolation<?>> constraintViolations) {
		constraintViolations.forEach(this::addValidationError);
  }


//  public HttpStatus getStatus() {
//    return status;
//  }
//
//  public void setStatus(HttpStatus status) {
//    this.status = status;
//  }
//
//  public String getMessage() {
//    return message;
//  }
//
//  public void setMessage(String message) {
//    this.message = message;
//  }
//
//  public String getErrorCode() {
//    return errorCode;
//  }
//
//  public void setErrorCode(String errorCode) {
//    this.errorCode = errorCode;
//  }
//
//  public LocalDateTime getTimestamp() {
//    return timestamp;
//  }
//
//  public void setTimestamp(LocalDateTime timestamp) {
//    this.timestamp = timestamp;
//  }
//
//  public String getDebugMessage() {
//    return debugMessage;
//  }
//
//  public void setDebugMessage(String debugMessage) {
//    this.debugMessage = debugMessage;
//  }
//
//  public List<ApiSubError> getSubErrors() {
//    return subErrors;
//  }
//
//  public void setSubErrors(List<ApiSubError> subErrors) {
//    this.subErrors = subErrors;
//  }
//
//  public T getData() {
//    return data;
//  }
//
//  public void setData(T data) {
//    this.data = data;
//  }

}