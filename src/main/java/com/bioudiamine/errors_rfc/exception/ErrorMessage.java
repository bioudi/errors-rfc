package com.bioudiamine.errors_rfc.exception;

public record ErrorMessage(String code, String message, String field) {
    public ErrorMessage(String code, String message) {
        this(code, message, null);
    }
}
