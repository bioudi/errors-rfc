package com.bioudiamine.errors_rfc.exception;

public enum ErrorCode {
    FREE_SALE_NOT_ALLOWED("BRE-C1-0001"),
    DISCOUNT_EXCEEDS_LIMIT("BRE-C1-0002"),
    INVALID_BASE_PRICE("BRE-C2-0001"),
    INVALID_DISCOUNT("BRE-C2-0002");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
