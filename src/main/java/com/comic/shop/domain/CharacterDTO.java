package com.comic.shop.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CharacterDTO {
    private List<Character> characters;

    @Data
    @Builder
    public static class Character {
        private String id;
        private String name;
        private String description;
        private String thumbnail;
        private int available;
        private int order;
    }
}