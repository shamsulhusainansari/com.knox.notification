package com.knox.notification.utils;

import com.knox.notification.exception.EmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.knox.notification.exception.ErrorMessageEnum.INVALID_TEMPLATE;

/**
 * Service for reading HTML templates from the classpath.
 */
@Slf4j
@Service
public class HtmlTemplateReader {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Reads an HTML file from the classpath and returns its content as a String.
     *
     * @param fileName The name of the HTML file (without extension).
     * @return The content of the HTML file as a String.
     * @throws EmailException if the template cannot be found or read.
     */
    public String getHtmlAsString(String fileName) {
        log.info("Attempting to read HTML template: {}", fileName);
        try {
            String path = "classpath:templates/" + fileName + ".html";
            log.debug("Resolved template path: {}", path);

            Resource resource = resourceLoader.getResource(path);

            if (!resource.exists()) {
                log.error("Template resource not found: {}", path);
                throw new EmailException(INVALID_TEMPLATE);
            }

            InputStream inputStream = resource.getInputStream();

            byte[] bytes = inputStream.readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            
            log.info("Successfully read HTML template: {}", fileName);
            return content;

        } catch (Exception e) {
            log.error("Error reading HTML template: {}", fileName, e);
            throw new EmailException(INVALID_TEMPLATE);
        }
    }

}
