package com.knox.notification.handler;

import com.knox.notification.constant.ChannelType;
import com.knox.notification.service.NotificationSender;
import com.knox.notification.service.impl.MailSenderService;
import com.knox.notification.service.impl.WhatsAppSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Factory class for managing and retrieving NotificationSender implementations.
 * Maps ChannelType to the corresponding NotificationSender.
 */
@Slf4j
@Component
public class NotificationFactory {

    private final Map<ChannelType, NotificationSender> senderMap;

    /**
     * Initializes the factory with available NotificationSender implementations.
     *
     * @param senders List of all available NotificationSender beans.
     */
    public NotificationFactory(List<NotificationSender> senders) {
        log.info("Initializing NotificationFactory with {} senders", senders.size());
        senderMap = Map.of(
                ChannelType.EMAIL, getSender(senders, MailSenderService.class),
//                ChannelType.SMS, getSender(senders, SmsSender.class),
                ChannelType.WHATSAPP, getSender(senders, WhatsAppSender.class)
        );
        log.info("NotificationFactory initialized with senders for channels: {}", senderMap.keySet());
    }

    /**
     * Helper method to find a specific sender type from the list of senders.
     *
     * @param list The list of available senders.
     * @param type The class type of the sender to find.
     * @param <T>  The type of the sender.
     * @return The found NotificationSender instance.
     * @throws IllegalStateException if the sender of the specified type is not found.
     */
    private <T> NotificationSender getSender(List<NotificationSender> list, Class<T> type) {
        log.debug("Searching for sender implementation: {}", type.getSimpleName());
        return list.stream()
                .filter(type::isInstance)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Notification sender implementation {} not found", type.getSimpleName());
                    return new IllegalStateException("Notification sender not found for type: " + type.getSimpleName());
                });
    }

    /**
     * Retrieves the NotificationSender for the specified channel type.
     *
     * @param type The channel type (e.g., EMAIL, WHATSAPP).
     * @return The corresponding NotificationSender.
     * @throws IllegalArgumentException if no sender is configured for the channel type.
     */
    public NotificationSender get(ChannelType type) {
        log.info("Retrieving notification sender for channel: {}", type);
        NotificationSender sender = senderMap.get(type);
        if (sender == null) {
            log.error("No sender configured for channel type: {}", type);
            throw new IllegalArgumentException("Unsupported channel type: " + type);
        }
        return sender;
    }
}
