package com.knox.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "whats-app")
public class WhatsAppConfig {

    private String url;
    private String token;
    private Map<String, String> template;

    public String getTemplateMessage(String key) {
        return template.get(key);
    }
}