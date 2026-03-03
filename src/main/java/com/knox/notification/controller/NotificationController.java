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

@Slf4j
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class NotificationController {

    private final SendMessageHandler handler;

    @PostMapping("/send")
    public Mono<NotificationResponse> send(@RequestBody NotificationRequest request) {
        log.info("Notification Request: {}", new Gson().toJson(request));
        return handler.handle(request);
    }
}
