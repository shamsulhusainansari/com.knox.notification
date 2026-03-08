package com.knox.notification.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.knox.notification.exception.ErrorMessage;
import com.knox.notification.exception.ErrorMessageEnum;
import com.knox.notification.exception.WhatsAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * WebClient wrapper for making external API calls.
 * Handles request execution and error mapping.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebClient {

    @Qualifier("whatsAppWebClient")
    private final WebClient webClient;

    /**
     * Executes a POST request to the configured external service.
     *
     * @param request      The request body object.
     * @param responseType The class type of the expected response.
     * @param <T>          The type of the request body.
     * @param <R>          The type of the response body.
     * @return A Mono containing the response object.
     */
    public <T, R> Mono<R> handlePostRequest(
            T request,
            Class<R> responseType) {
        log.info("Initiating POST request with payload: {}", new Gson().toJson(request));
        return webClient
                .post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType)
                .doOnSuccess(response -> log.info("POST request successful. Response: {}", new Gson().toJson(response)))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("WebClient request failed with status: {}", ex.getStatusCode());
                    try {
                        JsonNode root = ex.getResponseBodyAs(JsonNode.class);
                        if (root != null && root.has("error")) {
                            ErrorMessage error = new ObjectMapper().treeToValue(root.get("error"), ErrorMessage.class);
                            log.error("External API Error: {}", new Gson().toJson(error));
                            return Mono.error(new WhatsAppException(ErrorMessageEnum.errorMessageEnum(error.getCode())));
                        }
                    } catch (Exception e) {
                        log.error("Error parsing error response", e);
                    }
                    return Mono.error(ex);
                });
    }

}
