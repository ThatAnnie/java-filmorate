package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Event {
    @NotNull
    private Long eventId;
    @NotNull
    private Long timestamp;
    @NotNull
    private Long userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
    @NotNull
    private Long entityId;

}
