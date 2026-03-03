package com.knox.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private List<Message> messages;
    private String messaging_product;
    @Data
    static class Message{
        private String id;
        private String message_status;
    }
}
