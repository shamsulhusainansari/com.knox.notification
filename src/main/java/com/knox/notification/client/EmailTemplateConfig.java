package com.knox.notification.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "email-templates")
public class EmailTemplateConfig {

    private Map<String, TemplateDetails> templates;

    @Data
    public static class TemplateDetails {
        private String templateId;
        private String subjectLine;
    }
}
