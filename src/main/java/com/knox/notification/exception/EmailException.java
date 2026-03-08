package com.knox.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class EmailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6907996719611457114L;
    HttpStatus statusCode= HttpStatus.BAD_REQUEST;

    private String code;

    public EmailException(String message){
        super(message);
    }
    public EmailException(String code, String message){
        this(message);
        this.code = code;
    }

    public EmailException(ErrorMessageEnum error) {
        this(error.getCode(), error.getMessage());
        this.statusCode = HttpStatus.valueOf(Integer.parseInt(error.getHttpCode()));
    }

}
