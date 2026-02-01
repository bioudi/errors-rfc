package com.bioudiamine.errors_rfc.exception;

public class InvalidInputException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
