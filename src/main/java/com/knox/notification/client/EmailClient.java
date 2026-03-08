package com.knox.notification.client;

import com.knox.notification.config.EmailConfig;
import com.knox.notification.exception.EmailException;
import com.knox.notification.exception.ErrorMessageEnum;
import com.knox.notification.model.EmailRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Client for sending emails using JavaMail.
 * Handles SMTP session creation and email transmission.
 */
@Slf4j
@Component
public class EmailClient {

    private final EmailConfig emailConfig;
    private final Session session;

    public EmailClient(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
        this.session = createSession();
    }

    /**
     * Creates and configures the JavaMail Session.
     *
     * @return The configured Session object.
     */
    private Session createSession() {
        log.info("Initializing Email Session with host: {} and port: {}", emailConfig.getHost(), emailConfig.getPort());
        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getHost());
        props.put("mail.smtp.port", emailConfig.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        emailConfig.getUsername(),
                        emailConfig.getPassword()
                );
            }
        });
    }

    /**
     * Handles the email sending request.
     * Constructs the MIME message and sends it via Transport.
     *
     * @param request The email request containing recipient, subject, and body.
     * @throws EmailException if an error occurs during sending.
     */
    public void handleRequest(EmailRequest request) {
        log.info("Processing email request for recipient: {}", request.getTo());
        long start = System.currentTimeMillis();

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailConfig.getUsername()));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(validate(request.getTo())));

            message.setSubject(request.getSubject(), "UTF-8");

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(request.getBody(), "text/html; charset=UTF-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            message.setContent(multipart);

            log.debug("Dispatching email message via SMTP...");
            Transport.send(message);

            log.info("Email successfully sent to {} in {} ms",
                    request.getTo(),
                    System.currentTimeMillis() - start);

        } catch (Exception ex) {
            log.error("Failed to send email to {} after {} ms. Error: {}",
                    request.getTo(),
                    System.currentTimeMillis() - start,
                    ex.getMessage(), ex);

            throw new EmailException(ErrorMessageEnum.TECHNICAL_ERROR);
        }
    }

    /**
     * Validates the email address.
     *
     * @param email The email address to validate.
     * @return The validated email address.
     * @throws EmailException if the email is blank.
     */
    private String validate(String email) {
        if (StringUtils.isBlank(email)) {
            log.error("Email validation failed: Recipient email is blank");
            throw new EmailException(ErrorMessageEnum.TECHNICAL_ERROR);
        }
        return email;
    }
}
