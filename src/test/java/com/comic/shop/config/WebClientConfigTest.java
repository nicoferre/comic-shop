package com.comic.shop.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = "marvel.base.url=https://api.marvel.com")
public class WebClientConfigTest {

    @InjectMocks
    private WebClientConfig marvelWebClientConfig;

    @Value("${marvel.base.url}")
    private String baseUrl;

    @Test
    void testMarvelWebClientBeanCreation() {
        WebClient webClient = marvelWebClientConfig.marvelWebClient(baseUrl);
        assertNotNull(webClient);
    }
}