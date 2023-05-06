package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Rating> getRatings() {
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Rating getRating(@PathVariable Long id) {
        return ratingService.getRatingById(id);
    }
}
