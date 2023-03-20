package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

import javax.validation.constraints.*;

@Data
public class User {
    @NotNull
    private long id;
    @NotEmpty
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
