package com.knox.notification.service.impl;

import com.knox.notification.client.EmailClient;
import com.knox.notification.client.EmailTemplateConfig;
import com.knox.notification.exception.EmailException;
import com.knox.notification.exception.ErrorMessageEnum;
import com.knox.notification.model.EmailRequest;
import com.knox.notification.model.NotificationResponse;
import com.knox.notification.service.NotificationSender;
import com.knox.notification.utils.HtmlTemplateReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service implementation for sending email notifications.
 * Handles template resolution and delegates email sending to EmailClient.
 */
@Slf4j
@Service
public class MailSenderService implements NotificationSender {

    private final EmailClient emailClient;
    private final EmailTemplateConfig emailTemplateConfig;
    private final HtmlTemplateReader templateReader;

    public MailSenderService(EmailClient emailClient, EmailTemplateConfig emailTemplateConfig, HtmlTemplateReader templateReader) {
        this.emailClient = emailClient;
        this.emailTemplateConfig = emailTemplateConfig;
        this.templateReader = templateReader;
    }

    /**
     * Sends an email notification to the specified recipient.
     *
     * @param recipient The email address of the recipient.
     * @param content   The content ID used to resolve the email template.
     * @return A Mono containing the NotificationResponse.
     */
    @Override
    public Mono<NotificationResponse> send(String recipient, String content) {

        log.info("Initiating email send process for recipient: {} with contentId: {}", recipient, content);

        return Mono.justOrEmpty(emailTemplateConfig.getTemplates())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Email templates configuration is missing or empty");
                    return Mono.error(new EmailException(ErrorMessageEnum.INVALID_TEMPLATE));
                }))
                .flatMap(templates -> {
                    if (!templates.containsKey(content)) {
                        log.error("Template not found for contentId: {}", content);
                        return Mono.error(new IllegalArgumentException("Invalid contentId: " + content));
                    }
                    return Mono.just(templates.get(content));
                })
                .flatMap(template -> Mono.fromCallable(() -> {
                    log.info("Resolving template: {}", template.getTemplateId());

                    String htmlAsString = templateReader.getHtmlAsString(template.getTemplateId());

                    EmailRequest request = EmailRequest.builder()
                            .to(recipient)
                            .subject(template.getSubjectLine())
                            .body(htmlAsString)
                            .build();

                    log.debug("Delegating email request to EmailClient for recipient: {}", recipient);
                    emailClient.handleRequest(request);

                    log.info("Email sent successfully to recipient: {}", recipient);
                    return NotificationResponse.builder()
                            .messaging_product("email")
                            .build();

                }).subscribeOn(Schedulers.boundedElastic()));
    }
}
