package com.knox.notification.handler;

import com.knox.notification.constant.ChannelType;
import com.knox.notification.model.NotificationRequest;
import com.knox.notification.model.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class SendMessageHandler {
    private final NotificationFactory factory;

    public SendMessageHandler(NotificationFactory factory) {
        this.factory = factory;
    }

    public Mono<NotificationResponse> handle(NotificationRequest request) {
        log.info("Handling notification request for recipient: {}", request.getRecipient());

        ChannelType channelType = Optional.ofNullable(request.getChannel())
                .map(String::toUpperCase)
                .flatMap(value -> {
                    log.debug("Attempting to find ChannelType for value: {}", value);
                    return Arrays.stream(ChannelType.values())
                            .filter(type -> type.name().equals(value))
                            .findFirst();
                })
                .orElseGet(() -> {
                    log.warn("Channel not specified in request, defaulting to WHATSAPP");
                    return ChannelType.WHATSAPP;
                });

        log.info("Resolved channel type: {} for recipient: {}", channelType, request.getRecipient());

        return factory.get(channelType)
                .send(request.getRecipient(), request.getContentId())
                .doOnSuccess(response -> log.info("Successfully handled notification for recipient: {}", request.getRecipient()))
                .doOnError(error -> log.error("Error handling notification for recipient: {}", request.getRecipient(), error));
    }
}
