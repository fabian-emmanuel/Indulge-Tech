package com.indulgetech.exceptions;

import com.indulgetech.validators.ValidationErrors;
import org.springframework.validation.Errors;

public class InvalidRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Errors bindingResult;
	private ValidationErrors validationErrors;

	public InvalidRequestException(Errors bindingResult) {
		super("Invalid File Upload");
		this.bindingResult = bindingResult;
	}

    public InvalidRequestException(String message) {
		super(message);
    }

    public Errors getBindingResult() {
		return bindingResult;
	}

	public InvalidRequestException(ValidationErrors validationErrors) {
		super("Invalid File Upload");
		this.validationErrors = validationErrors;
	}

	public ValidationErrors getValidationErrors() {
		return validationErrors;
	}
}
