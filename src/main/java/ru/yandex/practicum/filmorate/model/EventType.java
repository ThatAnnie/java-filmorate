package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventType {
    LIKE("LIKE"),
    REVIEW("REVIEW"),
    FRIEND("FRIEND");
    @Getter
    private final String title;
}
