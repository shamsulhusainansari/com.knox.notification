package com.knox.notification.service.impl;

import com.knox.notification.client.NotificationWebClient;
import com.knox.notification.config.WhatsAppConfig;
import com.knox.notification.model.NotificationResponse;
import com.knox.notification.model.WhatsAppRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WhatsAppSenderTest {

    @Mock
    private NotificationWebClient notificationWebClient;

    @Mock
    private WhatsAppConfig whatsAppConfig;

    @InjectMocks
    private WhatsAppSender whatsAppSender;

    @BeforeEach
    void setUp() {
        when(whatsAppConfig.getTemplateMessage("welcome")).thenReturn("welcome_template");
    }

    @Test
    void send_ShouldReturnNotificationResponse_WhenSuccessful() {
        NotificationResponse expectedResponse = NotificationResponse.builder()
                .messaging_product("whatsapp")
                .build();

        when(notificationWebClient.handlePostRequest(any(WhatsAppRequest.class), eq(NotificationResponse.class)))
                .thenReturn(Mono.just(expectedResponse));

        Mono<NotificationResponse> result = whatsAppSender.send("1234567890", "welcome");

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void send_ShouldReturnError_WhenWebClientFails() {
        when(notificationWebClient.handlePostRequest(any(WhatsAppRequest.class), eq(NotificationResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("API Error")));

        Mono<NotificationResponse> result = whatsAppSender.send("1234567890", "welcome");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
