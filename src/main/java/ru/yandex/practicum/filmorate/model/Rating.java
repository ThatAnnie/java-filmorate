package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Rating {
    private long id;
    private String name;

    public Rating(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
