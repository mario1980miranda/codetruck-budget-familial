package com.decoder.budgetfamilial.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Turns on the native API versioning: the version travels in the
    // X-API-VERSION header, and only v1 is accepted by this application.
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader("X-API-VERSION")
                .addSupportedVersions("v1", "v2")
                .setDefaultVersion("v1");
    }
}
