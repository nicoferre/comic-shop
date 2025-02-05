package com.comic.shop.controller;

import com.comic.shop.domain.CharacterResponse;
import com.comic.shop.domain.Comic;
import com.comic.shop.domain.MarvelCharacter;
import com.comic.shop.domain.MarvelResponse;
import com.comic.shop.service.MarvelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComicShopControllerTest {

    @Mock
    private MarvelService marvelService;

    @InjectMocks
    private ComicShopController comicShopController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(comicShopController).build();
    }

    @Test
    void testGetComics() {
        MarvelResponse<Comic> mockResponse = new MarvelResponse<>();
        when(marvelService.getMarvelComics()).thenReturn(Mono.just(mockResponse));

        webTestClient.get()
                .uri("/v1/api/comics")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MarvelResponse.class);
    }

    @Test
    void testGetCharactersByComic() {
        CharacterResponse mockResponse = new CharacterResponse();
        when(marvelService.getCharactersByComicUPC(anyString())).thenReturn(Mono.just(mockResponse));

        webTestClient.get()
                .uri("/v1/api/comic/1234567890123/characters")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CharacterResponse.class);
    }

    @Test
    void testGetCharactersByName() {
        MarvelResponse<MarvelCharacter> mockResponse = new MarvelResponse<>();
        when(marvelService.getMarvelCharacters(anyString())).thenReturn(Mono.just(mockResponse));

        webTestClient.get()
                .uri("/v1/api/character/Spider-Man")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MarvelResponse.class);
    }
}