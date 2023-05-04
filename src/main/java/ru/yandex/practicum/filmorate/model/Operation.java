package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Operation {
    ADD("ADD"),
    UPDATE("UPDATE"),
    REMOVE("REMOVE");
    @Getter
    private final String title;
}
