package ru.yandex.practicum.filmorate.model;

public enum Operation {
    ADD("ADD"),
    UPDATE("UPDATE"),
    REMOVE("REMOVE");
    private final String title;

    Operation(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
