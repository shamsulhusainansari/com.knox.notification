# com.knox.notification

## Overview
This project is a notification service designed to handle message delivery across multiple channels, including Email and WhatsApp. It is built using Spring Boot and Project Reactor for reactive, non-blocking I/O operations.

## Features
- **Multi-Channel Support**: 
  - **Email**: Sends HTML emails using JavaMail.
  - **WhatsApp**: Integrates with WhatsApp Business API.
- **Factory Pattern**: Dynamically selects the appropriate notification sender based on the channel type.
- **Reactive Architecture**: Utilizes `Mono` and `Flux` for efficient, asynchronous processing.
- **Comprehensive Logging**: Detailed logging across services to track request flow and debug issues effectively.

## Key Components
- **`SendMessageHandler`**: The entry point for handling notification requests. It determines the channel and delegates to the appropriate sender.
- **`NotificationFactory`**: Manages the registration and retrieval of `NotificationSender` implementations (e.g., `MailSenderService`, `WhatsAppSender`).
- **`MailSenderService`**: Handles email composition and delivery.
- **`WhatsAppSender`**: Manages WhatsApp message construction and API interaction.
- **`EmailClient`**: A low-level client for SMTP interactions.

## Technologies
- Java
- Spring Boot
- Project Reactor
- JavaMail
- Lombok
- SLF4J (Logging)

## API Documentation

### Send Notification

**Endpoint:** `POST /api/v1/send`

**Description:** Sends a notification to a recipient via the specified channel (Email or WhatsApp).

**Request Body:**

```json
{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "contentId": "welcome_email"
}
```

| Field | Type | Description |
| :--- | :--- | :--- |
| `channel` | String | The channel to use for sending the notification. Supported values: `EMAIL`, `WHATSAPP`. |
| `recipient` | String | The recipient's email address or phone number. |
| `contentId` | String | The ID of the content template to use. |

**Response Body:**

```json
{
  "messaging_product": "email",
  "messages": [
    {
      "id": "msg_12345",
      "message_status": "sent"
    }
  ]
}
```

| Field | Type | Description |
| :--- | :--- | :--- |
| `messaging_product` | String | The messaging product used (e.g., "email", "whatsapp"). |
| `messages` | Array | A list of message details. |
| `messages[].id` | String | The unique identifier for the sent message. |
| `messages[].message_status` | String | The status of the message. |

## Code Overview

### Controller

The `NotificationController` exposes the `/api/v1/send` endpoint. It delegates the request handling to the `SendMessageHandler`.

```java
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class NotificationController {

    private final SendMessageHandler handler;

    @PostMapping("/send")
    public Mono<NotificationResponse> send(@RequestBody NotificationRequest request) {
        log.info("Notification Request: {}", new Gson().toJson(request));
        return handler.handle(request);
    }
}
```

### Handler

The `SendMessageHandler` determines the appropriate channel and uses the `NotificationFactory` to get the correct sender.

```java
@Component
public class SendMessageHandler {
    // ...
    public Mono<NotificationResponse> handle(NotificationRequest request) {
        // ... logic to determine channel type
        return factory.get(channelType)
                .send(request.getRecipient(), request.getContentId());
    }
}
```
