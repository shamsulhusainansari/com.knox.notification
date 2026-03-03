package com.knox.notification.client;

import com.knox.notification.config.WhatsAppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient whatsAppWebClient(WebClient.Builder builder, WhatsAppConfig whatsAppConfig) {
        return builder
                .baseUrl(whatsAppConfig.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " + whatsAppConfig.getToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
