package com.comic.shop.service;

import com.comic.shop.domain.CharacterResponse;
import com.comic.shop.domain.Comic;
import com.comic.shop.domain.MarvelCharacter;
import com.comic.shop.domain.MarvelResponse;
import com.comic.shop.utils.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarvelServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecRaw;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private MarvelService marvelService;

    @Captor
    private ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor;

    private final String testUPC = "1234567890123";
    private final String testPublicKey = "testPublicKey";
    private final String testHash = "testHash";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(marvelService, "publicKey", testPublicKey);
        when(hashGenerator.generateMD5Hash(anyString())).thenReturn(testHash);
    }

    @Test
    void testGetMarvelComicsByUPC_Success() {
        MarvelResponse<Comic> mockResponse = new MarvelResponse<>();
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpecRaw);
        when(requestHeadersUriSpecRaw.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockResponse));

        Mono<MarvelResponse<Comic>> result = marvelService.getMarvelComicsByUPC(testUPC);

        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();

        verify(webClient, times(1)).get();

        verify(requestHeadersUriSpecRaw).uri(uriCaptor.capture());
        Function<UriBuilder, URI> uriFunction = uriCaptor.getValue();
        URI generatedUri = uriFunction.apply(UriComponentsBuilder.fromPath(""));

        assertNotNull(generatedUri);
        assertTrue(generatedUri.toString().contains("/v1/public/comics"));
        assertTrue(generatedUri.toString().contains("upc=" + testUPC));
        assertTrue(generatedUri.toString().contains("apikey=" + testPublicKey));
        assertTrue(generatedUri.toString().contains("hash=" + testHash));
    }

    @Test
    void testGetMarvelComics_Success() {
        MarvelResponse<Comic> mockResponse = new MarvelResponse<>();

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpecRaw);
        when(requestHeadersUriSpecRaw.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockResponse));

        Mono<MarvelResponse<Comic>> result = marvelService.getMarvelComics();

        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();

        verify(webClient, times(1)).get();

        verify(requestHeadersUriSpecRaw).uri(uriCaptor.capture());
        Function<UriBuilder, URI> uriFunction = uriCaptor.getValue();
        URI generatedUri = uriFunction.apply(UriComponentsBuilder.fromPath(""));

        assertNotNull(generatedUri);
        assertTrue(generatedUri.toString().contains("/v1/public/comics"));
        assertTrue(generatedUri.toString().contains("apikey=" + testPublicKey));
        assertTrue(generatedUri.toString().contains("hash=" + testHash));
        assertTrue(generatedUri.toString().contains("ts="));
    }

    @Test
    void testGetMarvelCharacters_Success() {
        MarvelResponse<MarvelCharacter> mockResponse = new MarvelResponse<>();
        String testName = "Spider-Man";

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpecRaw);
        when(requestHeadersUriSpecRaw.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockResponse));

        Mono<MarvelResponse<MarvelCharacter>> result = marvelService.getMarvelCharacters(testName);

        StepVerifier.create(result)
                .expectNext(mockResponse)
                .verifyComplete();

        verify(webClient, times(1)).get();

        verify(requestHeadersUriSpecRaw).uri(uriCaptor.capture());
        Function<UriBuilder, URI> uriFunction = uriCaptor.getValue();
        URI generatedUri = uriFunction.apply(UriComponentsBuilder.fromPath(""));

        assertNotNull(generatedUri);
        assertTrue(generatedUri.toString().contains("/v1/public/characters"));
        assertTrue(generatedUri.toString().contains("name=" + testName));
        assertTrue(generatedUri.toString().contains("apikey=" + testPublicKey));
        assertTrue(generatedUri.toString().contains("hash=" + testHash));
    }

    @Test
    void testGetCharactersByComicUPC() {
        String upc = "5960605667-00111";

        Comic mockComic = new Comic();
        mockComic.setCharacters(Comic.Characters.builder()
                .items(List.of(new Comic.CharacterItem("Spider-Man")))
                .build());

        MarvelResponse<Comic> mockMarvelResponse = new MarvelResponse<>();
        mockMarvelResponse.setData(new MarvelResponse.DataContainer<>(List.of(mockComic)));

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpecRaw);
        when(requestHeadersUriSpecRaw.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(mockMarvelResponse));

        MarvelCharacter mockMarvelCharacter = new MarvelCharacter();
        mockMarvelCharacter.setId(1);
        mockMarvelCharacter.setName("Spider-Man");
        mockMarvelCharacter.setDescription("Hero description");
        mockMarvelCharacter.setThumbnail(new MarvelCharacter.Thumbnail("http://image", "jpg"));
        mockMarvelCharacter.setComics(new MarvelCharacter.Comics()); // Asegúrate de que comics no sea null

        MarvelResponse<MarvelCharacter> mockCharacterResponse = new MarvelResponse<>();
        mockCharacterResponse.setData(new MarvelResponse.DataContainer<>(List.of(mockMarvelCharacter)));

        when(marvelService.getMarvelCharacters(anyString())).thenReturn(Mono.just(mockCharacterResponse));

        Mono<CharacterResponse> result = marvelService.getCharactersByComicUPC(upc);

        StepVerifier.create(result)
                .expectNextMatches(characterResponse -> {
                    return characterResponse.getCharacters().size() == 1 &&
                            characterResponse.getCharacters().get(0).getName().equals("Spider-Man");
                })
                .verifyComplete();

        verify(webClient, times(3)).get();
        verify(requestHeadersUriSpecRaw, times(3)).uri(any(Function.class));
        verify(requestHeadersSpec, times(3)).retrieve();
        verify(responseSpec, times(2)).bodyToMono(any(ParameterizedTypeReference.class));
    }
}