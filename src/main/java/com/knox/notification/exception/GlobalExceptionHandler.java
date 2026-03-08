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

/**
 * Global exception handler for the application.
 * Intercepts exceptions and returns standardized error responses.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private AppConfig appConfig;

    /**
     * Handles WhatsAppException and returns a structured error response.
     *
     * @param ex The WhatsAppException thrown.
     * @return A Mono containing the ResponseEntity with the error details.
     */
    @ResponseBody
    @ExceptionHandler({WhatsAppException.class})
    public Mono<ResponseEntity<ErrorResponse>> handleWhatsAppException(WhatsAppException ex) {
        log.error("Handling WhatsAppException: {}", ex.getMessage(), ex);
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .origmesg(ex.getMessage())
                .build();
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorLocation(appConfig.getName())
                .errorId(String.valueOf(UUID.randomUUID()))
                .errorAt(LocalDateTime.now())
                .error(errorMessage)
                .build();

        return Mono.just(buildErrorResponse(ex.getStatusCode(), errorResponse));
    }

    /**
     * Builds a ResponseEntity containing the ErrorResponse.
     *
     * @param status    The HTTP status code.
     * @param errorResp The ErrorResponse object.
     * @return The ResponseEntity.
     */
    public static ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatusCode status, ErrorResponse errorResp) {
        String errorCode = extractErrorCodes(errorResp);
        logErrorResponse(errorResp);
        return ResponseEntity.status(status).header("error_code", errorCode).body(errorResp);
    }

    private static String extractErrorCodes(ErrorResponse errorResponse) {
        if (errorResponse.getError() != null && org.apache.commons.lang3.StringUtils.isNotBlank(errorResponse.getError().getCode())) {
            return errorResponse.getError().getCode();
        }
        return "ERR_AKG_00999";
    }

    private static void logErrorResponse(ErrorResponse er) {
        log.error("Returning ErrorResponse: {}", er);
    }
}
