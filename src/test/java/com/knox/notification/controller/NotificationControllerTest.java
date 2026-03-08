package com.knox.notification.controller;

import com.knox.notification.handler.SendMessageHandler;
import com.knox.notification.model.NotificationRequest;
import com.knox.notification.model.NotificationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private SendMessageHandler handler;

    @InjectMocks
    private NotificationController controller;

    @Test
    void send_ShouldReturnNotificationResponse_WhenSuccessful() {
        NotificationRequest request = NotificationRequest.builder()
                .channel("EMAIL")
                .recipient("test@example.com")
                .contentId("welcome")
                .build();

        NotificationResponse expectedResponse = NotificationResponse.builder()
                .messaging_product("email")
                .build();

        when(handler.handle(any(NotificationRequest.class))).thenReturn(Mono.just(expectedResponse));

        Mono<NotificationResponse> result = controller.send(request);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void send_ShouldReturnError_WhenHandlerFails() {
        NotificationRequest request = NotificationRequest.builder()
                .channel("EMAIL")
                .recipient("test@example.com")
                .contentId("welcome")
                .build();

        when(handler.handle(any(NotificationRequest.class))).thenReturn(Mono.error(new RuntimeException("Handler Error")));

        Mono<NotificationResponse> result = controller.send(request);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
