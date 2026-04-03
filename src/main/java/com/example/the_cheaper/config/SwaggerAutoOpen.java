package com.example.the_cheaper.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component
public class SwaggerAutoOpen implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String url = "http://localhost:8080/swagger-ui/index.html";

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("🚀 Swagger UI opened: " + url);
            } else {
                System.out.println("Desktop not supported. URL: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}