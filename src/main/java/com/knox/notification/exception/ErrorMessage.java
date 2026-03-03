package com.knox.notification.exception;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ErrorMessage {
    private String code;
    private String title;
    private String message;
    private String origmesg;
    public static ErrorMessage buildErrorMessage(String code, String message,  String origmesg) {
        return ErrorMessage.builder().code(code).message(message).origmesg(origmesg).build() ;
    }

}
