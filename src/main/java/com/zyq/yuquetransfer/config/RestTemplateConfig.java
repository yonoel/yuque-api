package com.zyq.yuquetransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value("${yuque.access_token}")
    private String accessToken;
    @Value("${yuque.cookie}")
    private String cookie;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(60000L))
                .setReadTimeout(Duration.ofMillis(60000L))
                .defaultHeader("X-Auth-Token", accessToken)
                .defaultHeader("cookie", cookie)
                .build();
    }
}
