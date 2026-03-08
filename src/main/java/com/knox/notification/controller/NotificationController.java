package com.knox.notification.controller;

import com.google.gson.Gson;
import com.knox.notification.handler.SendMessageHandler;
import com.knox.notification.model.NotificationRequest;
import com.knox.notification.model.NotificationResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST Controller for handling notification requests.
 * Exposes endpoints to send notifications via supported channels.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class NotificationController {

    private final SendMessageHandler handler;

    /**
     * Endpoint to send a notification.
     *
     * @param request The notification request payload containing recipient, channel, and content ID.
     * @return A Mono containing the NotificationResponse.
     */
    @PostMapping("/send")
    public Mono<NotificationResponse> send(@RequestBody NotificationRequest request) {
        log.info("Received notification request: {}", new Gson().toJson(request));
        return handler.handle(request)
                .doOnSuccess(response -> log.info("Notification request processed successfully"))
                .doOnError(error -> log.error("Error processing notification request", error));
    }
}
