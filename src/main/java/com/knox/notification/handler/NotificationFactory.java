package com.knox.notification.handler;

import com.knox.notification.constant.ChannelType;
import com.knox.notification.service.NotificationSender;
import com.knox.notification.service.impl.MailSenderService;
import com.knox.notification.service.impl.WhatsAppSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NotificationFactory {

    private final Map<ChannelType, NotificationSender> senderMap;

    public NotificationFactory(List<NotificationSender> senders) {
        log.info("Initializing NotificationFactory with {} senders", senders.size());
        senderMap = Map.of(
                ChannelType.EMAIL, getSender(senders, MailSenderService.class),
//                ChannelType.SMS, getSender(senders, SmsSender.class),
                ChannelType.WHATSAPP, getSender(senders, WhatsAppSender.class)
        );
        log.info("NotificationFactory initialized with senders for: {}", senderMap.keySet());
    }

    private <T> NotificationSender getSender(List<NotificationSender> list, Class<T> type) {
        log.debug("Searching for sender of type: {}", type.getSimpleName());
        return list.stream()
                .filter(type::isInstance)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Notification sender for type {} not found", type.getSimpleName());
                    return new IllegalStateException("Notification sender not found for type: " + type.getSimpleName());
                });
    }

    public NotificationSender get(ChannelType type) {
        log.info("Retrieving sender for channel type: {}", type);
        NotificationSender sender = senderMap.get(type);
        if (sender == null) {
            log.error("No sender configured for channel type: {}", type);
            throw new IllegalArgumentException("Unsupported channel type: " + type);
        }
        return sender;
    }
}
