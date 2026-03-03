package com.knox.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppRequest {

    private String messaging_product;

    private String to;
    private String type;
    private Template template;

    @Data
    @Builder
    public static class Template {
        private String name;
        private Language language;
    }

    @Data
    @Builder
    public static class Language {
        private String code;
    }
}