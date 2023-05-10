package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewGradesService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewGradesService reviewGradesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review saveReview(@RequestBody @Valid Review review) {
        return reviewService.saveReview(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviewsWithParam(@RequestParam(required = false) Long filmId,
                                            @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviewsWithParam(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDislikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.deleteDislikeFromReview(id, userId);
    }
}