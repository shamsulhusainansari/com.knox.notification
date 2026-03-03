package com.knox.notification.exception;

import lombok.Getter;

@Getter
public enum ErrorMessageEnum {

    UNAUTHORIZED("401","ERR_AKG_00108","invalid_api_key","Authentication failed. Please try again later or contact support."),
    TECHNICAL_ERROR("500","ERR_AKG_00999","INTERNAL_SERVER_ERROR","We could not process your request at this moment. Please try after some time."),
    INVALID_TEMPLATE("404","ERR_AKG_00999","132001","Invalid Template");
    private final String httpCode;

    private final String code;

    private final String verifyErrorCode;

    private final String message;

    ErrorMessageEnum(String httpCode, String code, String verifyErrorCode, String message) {
        this.httpCode = httpCode;
        this.code = code;
        this.verifyErrorCode = verifyErrorCode;
        this.message = message;
    }

    public static ErrorMessageEnum errorMessageEnum(String code) {
        for (int i = 0; i < ErrorMessageEnum.values().length; i++) {
            if (ErrorMessageEnum.values()[i].verifyErrorCode.equals(code.trim())) {
                return ErrorMessageEnum.values()[i];
            }
        }
        return TECHNICAL_ERROR;
    }

    @Override
    public String toString() {
        return code + ": " + verifyErrorCode + ": " + message;
    }


}
