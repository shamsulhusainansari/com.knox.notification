package com.knox.notification.service.impl;

import com.google.gson.Gson;
import com.knox.notification.client.NotificationWebClient;
import com.knox.notification.config.WhatsAppConfig;
import com.knox.notification.model.NotificationResponse;
import com.knox.notification.model.WhatsAppRequest;
import com.knox.notification.service.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.knox.notification.constant.NotificationConstants.*;

@Slf4j
@Service
public class WhatsAppSender implements NotificationSender {

    private final NotificationWebClient notificationWebClient;
    private final WhatsAppConfig whatsAppConfig;

    public WhatsAppSender(NotificationWebClient notificationWebClient, WhatsAppConfig whatsAppConfig) {
        this.notificationWebClient = notificationWebClient;
        this.whatsAppConfig = whatsAppConfig;
    }

    @Override
    public Mono<NotificationResponse> send(String recipient, String content) {
        log.info("Preparing to send WhatsApp message to recipient: {} with contentId: {}", recipient, content);
        String templateMessage = whatsAppConfig.getTemplateMessage(content);
        log.debug("Resolved template message: {}", templateMessage);

        WhatsAppRequest request = WhatsAppRequest.builder()
                .to(recipient)
                .messaging_product(WHATSAPP)
                .type(TYPE)
                .template(WhatsAppRequest.Template.builder()
                        .name(templateMessage)
                        .language(WhatsAppRequest.Language.builder().code(ENG).build())
                        .build())
                .build();

        log.info("Sending WhatsApp request payload: {}", new Gson().toJson(request));
        return notificationWebClient.handlePostRequest(request, NotificationResponse.class)
                .doOnSuccess(response -> log.info("WhatsApp message sent successfully to {}", recipient))
                .doOnError(error -> log.error("Failed to send WhatsApp message to {}", recipient, error));
    }
}
