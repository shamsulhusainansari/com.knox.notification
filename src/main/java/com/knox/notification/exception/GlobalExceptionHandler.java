package com.knox.notification.exception;

import com.knox.notification.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private AppConfig appConfig;

    @ResponseBody
    @ExceptionHandler({WhatsAppException.class})
    public Mono<ResponseEntity<ErrorResponse>> handleMaskingPatternException(WhatsAppException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder().code(ex.getCode()).message(ex.getMessage()).origmesg(ex.getMessage()).build();
        return Mono.just(buildErrorResponse(ex.getStatusCode(), ErrorResponse.builder().errorLocation(appConfig.getName()).errorId(String.valueOf(UUID.randomUUID())).errorAt(LocalDateTime.now()).error(errorMessage).build()));
    }

    public static ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatusCode status, ErrorResponse errorResp) {
        String errorCode = extractErrorCodes(errorResp);
        logErrorResponse(errorResp);
        return ResponseEntity.status(status).header("error_code", errorCode).body(errorResp);
    }
    private static String extractErrorCodes(ErrorResponse errorResponse) {
        if (errorResponse.getError() != null && org.apache.commons.lang3.StringUtils.isNotBlank(errorResponse.getError().getCode())) {
            return errorResponse.getError().getCode();
        }
        return "ERR_MPL_00999";
    }
    private static void logErrorResponse(ErrorResponse er) {
        log.error("ErrorResponse: {}",er);
    }
}