package com.comic.shop.controller;

import com.comic.shop.domain.CharacterResponse;
import com.comic.shop.domain.Comic;
import com.comic.shop.domain.MarvelCharacter;
import com.comic.shop.domain.MarvelResponse;
import com.comic.shop.service.MarvelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class ComicShopController {

    private final MarvelService marvelService;

    @GetMapping(value = "/comics", produces = "application/json")
    public Mono<ResponseEntity<MarvelResponse<Comic>>> getComics() {
        return marvelService.getMarvelComics()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping(value = "/comic/{UPC}/characters", produces = "application/json")
    public Mono<ResponseEntity<CharacterResponse>> getCharactersByComic(@PathVariable String UPC) {
        return marvelService.getCharactersByComicUPC(UPC)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping(value = "/character/{name}", produces = "application/json")
    public Mono<ResponseEntity<MarvelResponse<MarvelCharacter>>> getCharactersByName(@PathVariable String name) {
        return marvelService.getMarvelCharacters(name)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}