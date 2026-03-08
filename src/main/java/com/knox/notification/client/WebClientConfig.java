package com.knox.notification.client;

import com.knox.notification.config.WhatsAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient beans.
 * Sets up the WebClient for WhatsApp API communication.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * Creates a WebClient.Builder bean.
     *
     * @return A new WebClient.Builder instance.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Configures and creates a WebClient bean specifically for WhatsApp API.
     * Sets the base URL and default headers including Authorization.
     *
     * @param builder        The WebClient.Builder.
     * @param whatsAppConfig The WhatsApp configuration properties.
     * @return A configured WebClient instance.
     */
    @Bean
    public WebClient whatsAppWebClient(WebClient.Builder builder, WhatsAppConfig whatsAppConfig) {
        log.info("Configuring WhatsApp WebClient with Base URL: {}", whatsAppConfig.getUrl());
        return builder
                .baseUrl(whatsAppConfig.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " + whatsAppConfig.getToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
