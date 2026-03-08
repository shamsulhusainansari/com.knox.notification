package com.knox.notification.handler;

import com.knox.notification.constant.ChannelType;
import com.knox.notification.model.NotificationRequest;
import com.knox.notification.model.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

/**
 * Handler for processing notification requests.
 * Determines the channel type and delegates to the appropriate sender.
 */
@Slf4j
@Component
public class SendMessageHandler {
    private final NotificationFactory factory;

    public SendMessageHandler(NotificationFactory factory) {
        this.factory = factory;
    }

    /**
     * Handles the incoming notification request.
     * Resolves the channel type from the request and uses the factory to get the sender.
     * Defaults to WHATSAPP if no channel is specified or if the specified channel is invalid.
     *
     * @param request The notification request containing channel, recipient, and content.
     * @return A Mono containing the NotificationResponse.
     */
    public Mono<NotificationResponse> handle(NotificationRequest request) {
        log.info("Processing notification request for recipient: {}", request.getRecipient());

        ChannelType channelType = Optional.ofNullable(request.getChannel())
                .map(String::toUpperCase)
                .flatMap(value -> {
                    log.debug("Resolving channel type for value: {}", value);
                    return Arrays.stream(ChannelType.values())
                            .filter(type -> type.name().equals(value))
                            .findFirst();
                })
                .orElseGet(() -> {
                    log.warn("Channel type not specified or invalid. Defaulting to WHATSAPP.");
                    return ChannelType.WHATSAPP;
                });

        log.info("Selected channel: {} for recipient: {}", channelType, request.getRecipient());

        return factory.get(channelType)
                .send(request.getRecipient(), request.getContentId())
                .doOnSuccess(response -> log.info("Notification successfully handled for recipient: {}", request.getRecipient()))
                .doOnError(error -> log.error("Failed to handle notification for recipient: {}", request.getRecipient(), error));
    }
}
