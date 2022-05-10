package com.indulgetech.exceptions;

public class UnAuthorizeEntityAccessException extends RuntimeException {
    public UnAuthorizeEntityAccessException(String message) {
        super(message);
    }

    public UnAuthorizeEntityAccessException(EntityType country, String message) {
    }

    public UnAuthorizeEntityAccessException(EntityType entityType) {
        super("Access denied: Trying to access resource:"+entityType+" which does not belong to your organisation");
    }

    public UnAuthorizeEntityAccessException() {
        super("Unauthorised resource");
    }
}
