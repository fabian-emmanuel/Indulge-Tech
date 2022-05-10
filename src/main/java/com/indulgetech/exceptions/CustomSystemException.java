package com.indulgetech.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CustomSystemException extends RuntimeException{

    protected String message;

    public CustomSystemException(String message) {
        this.message=message;
    }

    public CustomSystemException(Throwable cause) {
        super(cause);
    }

    public CustomSystemException(String message, Throwable cause) {
        super(message, cause);
        this.message=message;
    }
}
