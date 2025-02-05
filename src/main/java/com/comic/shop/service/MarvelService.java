package com.comic.shop.service;

import com.comic.shop.domain.CharacterDTO;
import com.comic.shop.domain.CharacterResponse;
import com.comic.shop.domain.Comic;
import com.comic.shop.domain.MarvelCharacter;
import com.comic.shop.domain.MarvelResponse;
import com.comic.shop.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarvelService {

    private final WebClient webClient;

    private final HashGenerator hashGenerator;

    @Value("${marvel.public.key}")
    private String publicKey;

    public Mono<CharacterResponse> getCharactersByComicUPC(String upc) {
        return getMarvelComicsByUPC(upc)
                .flatMap(response -> {
                    List<Comic> comics = response.getData().getResults();
                    if (comics.isEmpty()) {
                        return Mono.empty();
                    }
                    Comic comic = comics.get(0);

                    List<Mono<CharacterDTO.Character>> characterMonos = comic.getCharacters().getItems().stream()
                            .map(item -> getMarvelCharacters(item.getName())
                                    .map(characterResponse -> mapToCharacterDTO(characterResponse)))
                            .collect(Collectors.toList());

                    return Mono.zip(characterMonos, characters -> {
                        List<CharacterDTO.Character> characterList = Arrays.stream(characters)
                                .filter(Objects::nonNull)
                                .map(CharacterDTO.Character.class::cast)
                                .sorted((c1, c2) -> Integer.compare(c2.getAvailable(), c1.getAvailable()))
                                .toList();

                        for (int i = 0; i < characterList.size(); i++) {
                            characterList.get(i).setOrder(i + 1);
                        }

                        return buildCharacterResponse(characterList);
                    });
                });
    }

    private CharacterDTO.Character mapToCharacterDTO(MarvelResponse<MarvelCharacter> characterResponse) {
        MarvelResponse.DataContainer<MarvelCharacter> dataContainer = characterResponse.getData();
        if (dataContainer.getResults().isEmpty()) {
            return null;
        }
        MarvelCharacter character = dataContainer.getResults().get(0);
        return CharacterDTO.Character.builder()
                .id(String.valueOf(character.getId()))
                .name(character.getName())
                .description(character.getDescription())
                .thumbnail(character.getThumbnail().getPath() + "." + character.getThumbnail().getExtension())
                .available(character.getComics().getAvailable())
                .build();
    }

    private CharacterResponse buildCharacterResponse(List<CharacterDTO.Character> characterList) {
        List<CharacterResponse.Character> responseCharacterList = characterList.stream()
                .map(dtoCharacter -> CharacterResponse.Character.builder()
                        .id(dtoCharacter.getId())
                        .name(dtoCharacter.getName())
                        .description(dtoCharacter.getDescription())
                        .thumbnail(dtoCharacter.getThumbnail())
                        .order(dtoCharacter.getOrder())
                        .build())
                .collect(Collectors.toList());

        return CharacterResponse.builder()
                .characters(responseCharacterList)
                .build();
    }

    public Mono<MarvelResponse<MarvelCharacter>> getMarvelCharacters(String name) {
        String timestamp = String.valueOf(java.time.Instant.now().getEpochSecond());
        String apiHash = hashGenerator.generateMD5Hash(timestamp);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/public/characters")
                        .queryParam("name", name)
                        .queryParam("ts", timestamp)
                        .queryParam("apikey", publicKey)
                        .queryParam("hash", apiHash)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MarvelResponse<MarvelCharacter>>() {});
    }

    public Mono<MarvelResponse<Comic>>  getMarvelComics() {
        String timestamp = String.valueOf(java.time.Instant.now().getEpochSecond());
        String apiHash = hashGenerator.generateMD5Hash(timestamp);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/public/comics")
                        .queryParam("apikey", publicKey)
                        .queryParam("hash", apiHash)
                        .queryParam("ts", timestamp)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MarvelResponse<Comic>>() {});
    }

    public Mono<MarvelResponse<Comic>> getMarvelComicsByUPC(String upc) {
        String timestamp = String.valueOf(java.time.Instant.now().getEpochSecond());
        String apiHash = hashGenerator.generateMD5Hash(timestamp);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/public/comics")
                        .queryParam("upc", upc)
                        .queryParam("apikey", publicKey)
                        .queryParam("hash", apiHash)
                        .queryParam("ts", timestamp)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MarvelResponse<Comic>>() {});

    }

}