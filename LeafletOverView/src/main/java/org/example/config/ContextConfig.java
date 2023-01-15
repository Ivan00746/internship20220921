package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
public class ContextConfig {

    @Bean
    @SessionScope
    public RestTemplate rtOpenStreetMap() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
