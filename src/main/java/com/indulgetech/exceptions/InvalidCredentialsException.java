package com.indulgetech.exceptions;

public class InvalidCredentialsException extends GenericRuntimeException {

  private final static String ERROR_CODE = "400";

  private static final long serialVersionUID = 1L;

  public InvalidCredentialsException() {
    super("Invalid credentials");
  }

  public InvalidCredentialsException(String errorCode, String message) {
    super(errorCode,message);
  }

  public InvalidCredentialsException(String message) {
    super(ERROR_CODE,message);
  }

  public InvalidCredentialsException(String message, Throwable e) {
    super(message,e);
    this.setErrorCode(ERROR_CODE);
  }
}
