package com.knox.notification.client;

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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebClient {

    @Qualifier("whatsAppWebClient")
    private final WebClient webClient;

    public <T, R> Mono<R> handlePostRequest(
            T request,
            Class<R> responseType) {
        return webClient
                .post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    JsonNode root = ex.getResponseBodyAs(JsonNode.class);
                    ErrorMessage error = new ObjectMapper().treeToValue(root.get("error"), ErrorMessage.class);
                    log.info(new Gson().toJson(error));
                    return Mono.error(new WhatsAppException(ErrorMessageEnum.errorMessageEnum(error.getCode())));
                });
    }

}
