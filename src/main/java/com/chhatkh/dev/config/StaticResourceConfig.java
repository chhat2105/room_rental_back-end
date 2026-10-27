package com.chhatkh.dev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.*;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Value("${app.upload-dir:uploads}") private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**").addResourceLocations(root.toUri().toString());
    }
}
