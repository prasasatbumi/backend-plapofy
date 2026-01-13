package com.finprov.loan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the 'uploads/' directory via '/api/files/'
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:uploads/");
    }
}
