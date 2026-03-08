package com.knox.notification.handler;

import com.knox.notification.constant.ChannelType;
import com.knox.notification.model.NotificationRequest;
import com.knox.notification.model.NotificationResponse;
import com.knox.notification.service.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMessageHandlerTest {

    @Mock
    private NotificationFactory factory;

    @Mock
    private NotificationSender sender;

    @InjectMocks
    private SendMessageHandler handler;

    @BeforeEach
    void setUp() {
        when(factory.get(any(ChannelType.class))).thenReturn(sender);
    }

    @Test
    void handle_ShouldUseEmailSender_WhenChannelIsEmail() {
        NotificationRequest request = NotificationRequest.builder()
                .channel("EMAIL")
                .recipient("test@example.com")
                .contentId("welcome")
                .build();

        when(sender.send(anyString(), anyString())).thenReturn(Mono.just(new NotificationResponse()));

        Mono<NotificationResponse> result = handler.handle(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(factory).get(ChannelType.EMAIL);
        verify(sender).send("test@example.com", "welcome");
    }

    @Test
    void handle_ShouldUseWhatsAppSender_WhenChannelIsWhatsApp() {
        NotificationRequest request = NotificationRequest.builder()
                .channel("WHATSAPP")
                .recipient("1234567890")
                .contentId("welcome")
                .build();

        when(sender.send(anyString(), anyString())).thenReturn(Mono.just(new NotificationResponse()));

        Mono<NotificationResponse> result = handler.handle(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(factory).get(ChannelType.WHATSAPP);
        verify(sender).send("1234567890", "welcome");
    }

    @Test
    void handle_ShouldDefaultToWhatsApp_WhenChannelIsMissing() {
        NotificationRequest request = NotificationRequest.builder()
                .recipient("1234567890")
                .contentId("welcome")
                .build();

        when(sender.send(anyString(), anyString())).thenReturn(Mono.just(new NotificationResponse()));

        Mono<NotificationResponse> result = handler.handle(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(factory).get(ChannelType.WHATSAPP);
        verify(sender).send("1234567890", "welcome");
    }
}
