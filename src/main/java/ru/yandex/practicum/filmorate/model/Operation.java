package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Operation {
    ADD("ADD"),
    UPDATE("UPDATE"),
    REMOVE("REMOVE");
    @Getter
    private final String title;
}
