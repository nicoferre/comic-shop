package com.comic.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_ACCEPT_VALUE = MediaType.APPLICATION_JSON_VALUE;

    @Bean(name = "marvelWebClient")
    public WebClient marvelWebClient(@Value("${marvel.base.url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HEADER_ACCEPT, HEADER_ACCEPT_VALUE)
                .build();
    }
}