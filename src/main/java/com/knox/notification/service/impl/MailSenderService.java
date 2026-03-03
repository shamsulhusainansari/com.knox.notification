package com.knox.notification.service.impl;

import com.knox.notification.client.EmailClient;
import com.knox.notification.client.EmailTemplateConfig;
import com.knox.notification.model.EmailRequest;
import com.knox.notification.model.NotificationResponse;
import com.knox.notification.service.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class MailSenderService implements NotificationSender {

    private final EmailClient emailClient;
    private final EmailTemplateConfig emailTemplateConfig;

    public MailSenderService(EmailClient emailClient, EmailTemplateConfig emailTemplateConfig) {
        this.emailClient = emailClient;
        this.emailTemplateConfig = emailTemplateConfig;
    }

    @Override
    public Mono<NotificationResponse> send(String recipient, String content) {
        log.info("Sending email to {} with contentId {}", recipient, content);
        return Mono.justOrEmpty(emailTemplateConfig.getTemplates())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Email templates not configured");
                    return Mono.error(new IllegalStateException("Email templates not configured"));
                }))
                .map(templates -> templates.get(content))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Invalid contentId: {}", content);
                    return Mono.error(new IllegalArgumentException("Invalid contentId: " + content));
                }))
                .flatMap(template -> Mono.fromCallable(() -> {
                    log.info("Building email request for recipient {}", recipient);
                    EmailRequest request = EmailRequest.builder()
                            .to(recipient)
                            .subject(template.getSubjectLine())
                            .body(dummyTemplate())
                            .build();

                    emailClient.handleRequest(request);

                    log.info("Email request sent for recipient {}", recipient);
                    return NotificationResponse.builder()
                            .messaging_product("email")
                            .build();

                }).subscribeOn(Schedulers.boundedElastic()));
    }


    public static String dummyTemplate() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Welcome Email</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f6f8;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 40px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #4CAF50;\n" +
                "            color: white;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 30px;\n" +
                "            color: #333333;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 20px;\n" +
                "            margin-top: 20px;\n" +
                "            background-color: #4CAF50;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 4px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 15px;\n" +
                "            font-size: 12px;\n" +
                "            color: #888888;\n" +
                "            background-color: #f0f0f0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "        <h2>Welcome to Our Community \uD83C\uDF89</h2>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"content\">\n" +
                "        <p>Hi <strong>{{UserName}}</strong>,</p>\n" +
                "\n" +
                "        <p>We’re excited to have you on board! Thank you for joining us. Your account has been successfully created, and you're all set to get started.</p>\n" +
                "\n" +
                "        <p>Click the button below to explore and begin your journey with us.</p>\n" +
                "\n" +
                "        <a href=\"{{ActionLink}}\" class=\"button\">Get Started</a>\n" +
                "\n" +
                "        <p>If you have any questions, feel free to reply to this email. We're here to help!</p>\n" +
                "\n" +
                "        <p>Best Regards,<br>\n" +
                "        <strong>Your Company Team</strong></p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"footer\">\n" +
                "        © 2026 Your Company. All rights reserved.\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }
}
