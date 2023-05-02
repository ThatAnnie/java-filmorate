package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.review.grades.ReviewGradesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
@SqlGroup({
        @Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewTest {

    @Autowired
    private final UserDbStorage userDbStorage;
    @Autowired
    private final FilmDbStorage filmDbStorage;
    @Autowired
    private final ReviewStorage reviewStorage;
    @Autowired
    private final ReviewGradesStorage reviewGradesStorage;
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    void deleteFilmsFromDB() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
        String sql2 = "DELETE FROM users";
        jdbcTemplate.update(sql2);
    }

    @Test
    void testSaveAndGetByIdReview() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);

        Rating mpa = new Rating(2, "PG");
        Film film = new Film();
        film.setName("FilmName4");
        film.setDescription("Description4");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2010, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);

        Review review = new Review();
        review.setContent("It's good film");
        review.setIsPositive(true);
        review.setUserId(userDB.getId());
        review.setFilmId(filmDB.getId());
        review.setUseful(0L);

        Review reviewDB = reviewStorage.save(review);
        Long id = reviewDB.getReviewId();
        Review reviewCheck = reviewStorage.getById(id).get();

        Assertions.assertEquals(reviewDB, reviewCheck);
    }

    @Test
    void testUpdateReview() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);

        Rating mpa = new Rating(2, "PG");
        Film film = new Film();
        film.setName("FilmName4");
        film.setDescription("Description4");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2010, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);

        Review review = new Review();
        review.setContent("It's good film");
        review.setIsPositive(true);
        review.setUserId(userDB.getId());
        review.setFilmId(filmDB.getId());
        review.setUseful(0L);
        Review reviewDB = reviewStorage.save(review);
        Long id = reviewDB.getReviewId();

        Review reviewUpdate = new Review();
        reviewUpdate.setReviewId(id);
        reviewUpdate.setContent("It's bad film");
        reviewUpdate.setIsPositive(false);
        reviewUpdate.setUserId(userDB.getId());
        reviewUpdate.setFilmId(filmDB.getId());
        reviewUpdate.setUseful(0L);

        reviewStorage.update(reviewUpdate);
        Review reviewCheck = reviewStorage.getById(id).get();

        Assertions.assertEquals(reviewUpdate, reviewCheck);
    }

    @Test
    void testGetListReviews() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB1 = userDbStorage.save(user);

        User user2 = new User();
        user.setName("UserName2");
        user.setLogin("login2");
        user.setEmail("test2@test2.ru");
        user.setBirthday(LocalDate.of(1990, 03, 2));
        User userDB2 = userDbStorage.save(user);

        Rating mpa = new Rating(2, "PG");
        Film film = new Film();
        film.setName("FilmName4");
        film.setDescription("Description4");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2010, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);

        Review review = new Review();
        review.setContent("It's good film");
        review.setIsPositive(true);
        review.setUserId(userDB1.getId());
        review.setFilmId(filmDB.getId());
        review.setUseful(0L);
        reviewStorage.save(review);

        Review review2 = new Review();
        review2.setContent("It's bad film");
        review2.setIsPositive(false);
        review2.setUserId(userDB2.getId());
        review2.setFilmId(filmDB.getId());
        review2.setUseful(0L);
        reviewStorage.save(review2);

        List<Review> reviews = reviewStorage.getList();
        assertThat(reviews.size()).isEqualTo(2);
    }

    @Test
    void testAddLikeDeleteLikeToReview() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long userId = userDB.getId();

        Rating mpa = new Rating(2, "PG");
        Film film = new Film();
        film.setName("FilmName4");
        film.setDescription("Description4");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2010, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);

        Review review = new Review();
        review.setContent("It's good film");
        review.setIsPositive(true);
        review.setUserId(userDB.getId());
        review.setFilmId(filmDB.getId());
        review.setUseful(0L);
        Review reviewDB = reviewStorage.save(review);
        Long id = reviewDB.getReviewId();

        reviewGradesStorage.addLike(id, userId);
        Assertions.assertEquals(reviewStorage.getById(id).get().getUseful(), 1);
        reviewGradesStorage.deleteLike(id, userId);
        Assertions.assertEquals(reviewStorage.getById(id).get().getUseful(), 0);

        reviewGradesStorage.addDislike(id, userId);
        Assertions.assertEquals(reviewStorage.getById(id).get().getUseful(), -1);
        reviewGradesStorage.deleteDislike(id, userId);
        Assertions.assertEquals(reviewStorage.getById(id).get().getUseful(), 0);
    }
}