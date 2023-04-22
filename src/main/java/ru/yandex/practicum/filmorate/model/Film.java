package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class Film {
    @NotNull
    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private final Set<Long> usersLikes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();
    private Rating mpa;

    public static Integer getNumberOfLikes(Film film) {
        return film.getUsersLikes().size();
    }
}
