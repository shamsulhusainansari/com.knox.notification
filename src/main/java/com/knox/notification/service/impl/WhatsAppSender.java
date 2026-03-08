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

/**
 * Service implementation for sending WhatsApp notifications.
 * Constructs the WhatsApp request and delegates to NotificationWebClient.
 */
@Slf4j
@Service
public class WhatsAppSender implements NotificationSender {

    private final NotificationWebClient notificationWebClient;
    private final WhatsAppConfig whatsAppConfig;

    public WhatsAppSender(NotificationWebClient notificationWebClient, WhatsAppConfig whatsAppConfig) {
        this.notificationWebClient = notificationWebClient;
        this.whatsAppConfig = whatsAppConfig;
    }

    /**
     * Sends a WhatsApp notification to the specified recipient.
     *
     * @param recipient The phone number of the recipient.
     * @param content   The content ID used to resolve the WhatsApp template.
     * @return A Mono containing the NotificationResponse.
     */
    @Override
    public Mono<NotificationResponse> send(String recipient, String content) {
        log.info("Initiating WhatsApp message send process for recipient: {} with contentId: {}", recipient, content);
        
        String templateMessage = whatsAppConfig.getTemplateMessage(content);
        log.debug("Resolved WhatsApp template message name: {}", templateMessage);

        WhatsAppRequest request = WhatsAppRequest.builder()
                .to(recipient)
                .messaging_product(WHATSAPP)
                .type(TYPE)
                .template(WhatsAppRequest.Template.builder()
                        .name(templateMessage)
                        .language(WhatsAppRequest.Language.builder().code(ENG).build())
                        .build())
                .build();

        log.info("Constructed WhatsApp request payload: {}", new Gson().toJson(request));
        
        return notificationWebClient.handlePostRequest(request, NotificationResponse.class)
                .doOnSuccess(response -> log.info("WhatsApp message successfully sent to recipient: {}", recipient))
                .doOnError(error -> log.error("Failed to send WhatsApp message to recipient: {}", recipient, error));
    }
}
