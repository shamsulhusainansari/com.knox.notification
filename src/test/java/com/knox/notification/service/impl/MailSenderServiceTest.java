package com.knox.notification.service.impl;

import com.knox.notification.client.EmailClient;
import com.knox.notification.client.EmailTemplateConfig;
import com.knox.notification.exception.EmailException;
import com.knox.notification.model.EmailRequest;
import com.knox.notification.model.NotificationResponse;
import com.knox.notification.utils.HtmlTemplateReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private EmailClient emailClient;

    @Mock
    private EmailTemplateConfig emailTemplateConfig;

    @Mock
    private HtmlTemplateReader templateReader;

    @InjectMocks
    private MailSenderService mailSenderService;

    private Map<String, EmailTemplateConfig.TemplateDetails> templates;

    @BeforeEach
    void setUp() {
        templates = new HashMap<>();
        EmailTemplateConfig.TemplateDetails details = new EmailTemplateConfig.TemplateDetails();
        details.setTemplateId("welcome_template");
        details.setSubjectLine("Welcome!");
        templates.put("welcome", details);
    }

    @Test
    void send_ShouldReturnNotificationResponse_WhenTemplateExists() {
        when(emailTemplateConfig.getTemplates()).thenReturn(templates);
        when(templateReader.getHtmlAsString("welcome_template")).thenReturn("<html><body>Welcome</body></html>");
        doNothing().when(emailClient).handleRequest(any(EmailRequest.class));

        Mono<NotificationResponse> result = mailSenderService.send("test@example.com", "welcome");

        StepVerifier.create(result)
                .expectNextMatches(response -> "email".equals(response.getMessaging_product()))
                .verifyComplete();

        verify(emailClient, times(1)).handleRequest(any(EmailRequest.class));
    }

    @Test
    void send_ShouldReturnError_WhenTemplatesNotConfigured() {
        when(emailTemplateConfig.getTemplates()).thenReturn(null);

        Mono<NotificationResponse> result = mailSenderService.send("test@example.com", "welcome");

        StepVerifier.create(result)
                .expectError(EmailException.class)
                .verify();
    }

    @Test
    void send_ShouldReturnError_WhenContentIdInvalid() {
        when(emailTemplateConfig.getTemplates()).thenReturn(templates);

        Mono<NotificationResponse> result = mailSenderService.send("test@example.com", "invalid_content");

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
