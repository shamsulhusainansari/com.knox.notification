package com.knox.notification.service;

import com.knox.notification.model.NotificationResponse;
import reactor.core.publisher.Mono;

public interface NotificationSender {
    Mono<NotificationResponse> send(String recipient, String content);
}
