package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final FriendshipDbStorage friendshipDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    void deleteFilmsFromDB() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
        String sql2 = "DELETE FROM users";
        jdbcTemplate.update(sql2);
    }

    @Test
    void testGetGenres() {
        List<Genre> genres = genreDbStorage.getList();
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    void testGetGenreById() {
        Genre genre = genreDbStorage.getById(1L).get();
        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void testGetRating() {
        List<Rating> ratings = ratingDbStorage.getList();
        assertThat(ratings.size()).isEqualTo(5);
    }

    @Test
    void testGetRatingById() {
        Rating rating = ratingDbStorage.getById(1L).get();
        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getName()).isEqualTo("G");
    }

    @Test
    void testSaveFilm() {
        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);
        Long id = filmDB.getId();
        Film filmCheck = filmDbStorage.getById(id).get();
        assertThat(filmCheck.getName()).isEqualTo("FilmName");
        assertThat(filmCheck.getDescription()).isEqualTo("Description");
        assertThat(filmCheck.getDuration()).isEqualTo(100);
        assertThat(filmCheck.getMpa()).isEqualTo(mpa);
    }

    @Test
    void testUpdateFilm() {
        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName2");
        film.setDescription("Description2");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2003, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);
        Long id = filmDB.getId();
        Film filmUpdate = new Film();
        filmUpdate.setName("FilmName2");
        filmUpdate.setDescription("DescriptionNew2");
        filmUpdate.setDuration(100);
        filmUpdate.setReleaseDate(LocalDate.of(2003, 02, 9));
        filmUpdate.setMpa(mpa);
        filmUpdate.setId(id);
        filmDbStorage.update(filmUpdate);
        Film filmCheck = filmDbStorage.getById(id).get();

        assertThat(filmCheck.getName()).isEqualTo("FilmName2");
        assertThat(filmCheck.getDescription()).isEqualTo("DescriptionNew2");
        assertThat(filmCheck.getDuration()).isEqualTo(100);
        assertThat(filmCheck.getMpa()).isEqualTo(mpa);
    }

    @Test
    void testGetListFilms() {
        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName2");
        film.setDescription("Description2");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2003, 02, 9));
        film.setMpa(mpa);
        filmDbStorage.save(film);
        Film film2 = new Film();
        film2.setName("FilmName2");
        film2.setDescription("DescriptionNew2");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2003, 02, 9));
        film2.setMpa(mpa);
        filmDbStorage.save(film2);
        List<Film> films = filmDbStorage.getList();
        assertThat(films.size()).isEqualTo(2);
    }

    @Test
    void testGetByIdFilm() {
        Rating mpa = new Rating(2, "PG");
        Film film = new Film();
        film.setName("FilmName3");
        film.setDescription("Description3");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2010, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);
        Long id = filmDB.getId();
        Film filmCheck = filmDbStorage.getById(id).get();
        assertThat(filmCheck.getName()).isEqualTo("FilmName3");
        assertThat(filmCheck.getDescription()).isEqualTo("Description3");
        assertThat(filmCheck.getDuration()).isEqualTo(120);
        assertThat(filmCheck.getMpa()).isEqualTo(mpa);
    }

    @Test
    void testAddLikeDeleteLike() {
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
        likeDbStorage.addLike(filmDB.getId(), userDB.getId());
        likeDbStorage.deleteLike(filmDB.getId(), userDB.getId());
        Film filmCheck = filmDbStorage.getById(filmDB.getId()).get();
        assertThat(likeDbStorage.getUsersLikesByFilm(filmCheck.getId()).size()).isEqualTo(0);
    }

    @Test
    void testGetPopularFilms() {
        User user2 = new User();
        user2.setName("UserName2");
        user2.setLogin("login2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB2 = userDbStorage.save(user2);
        User user3 = new User();
        user3.setName("UserName3");
        user3.setLogin("login3");
        user3.setEmail("test3@test.ru");
        user3.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB3 = userDbStorage.save(user3);
        User user4 = new User();
        user4.setName("UserName4");
        user4.setLogin("login4");
        user4.setEmail("test4@test.ru");
        user4.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB4 = userDbStorage.save(user4);
        Rating mpa = new Rating(2, "PG");
        Film film1 = new Film();
        film1.setName("FilmName1");
        film1.setDescription("Description1");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(2010, 02, 9));
        film1.setMpa(mpa);
        Film filmDB1 = filmDbStorage.save(film1);
        Rating mpa2 = new Rating(1, "G");
        Film film2 = new Film();
        film2.setName("FilmName2");
        film2.setDescription("Description2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(2010, 02, 9));
        film2.setMpa(mpa2);
        Film filmDB2 = filmDbStorage.save(film2);
        Film film3 = new Film();
        film3.setName("FilmName3");
        film3.setDescription("Description3");
        film3.setDuration(120);
        film3.setReleaseDate(LocalDate.of(2010, 02, 9));
        film3.setMpa(mpa);
        Film filmDB3 = filmDbStorage.save(film3);
        likeDbStorage.addLike(filmDB1.getId(), userDB2.getId());
        likeDbStorage.addLike(filmDB1.getId(), userDB3.getId());
        likeDbStorage.addLike(filmDB1.getId(), userDB4.getId());
        likeDbStorage.addLike(filmDB3.getId(), userDB2.getId());
        likeDbStorage.addLike(filmDB3.getId(), userDB4.getId());
        likeDbStorage.addLike(filmDB2.getId(), userDB3.getId());
        Collection<Film> popularFilms = likeDbStorage.getPopularFilms(2);
        assertThat(popularFilms.size()).isEqualTo(2);
        assertThat(popularFilms.contains(filmDB1)).isTrue();
        assertThat(popularFilms.contains(filmDB3)).isTrue();
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long id = userDB.getId();
        User userCheck = userDbStorage.getById(id).get();
        assertThat(userCheck.getName()).isEqualTo("UserName");
        assertThat(userCheck.getLogin()).isEqualTo("login");
        assertThat(userCheck.getEmail()).isEqualTo("test@test.ru");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long id = userDB.getId();
        User userUpdate = new User();
        userUpdate.setName("UserNameNew");
        userUpdate.setLogin("login");
        userUpdate.setEmail("test@test.ru");
        userUpdate.setBirthday(LocalDate.of(1990, 03, 9));
        userUpdate.setId(id);
        userDbStorage.update(userUpdate);
        User userCheck = userDbStorage.getById(id).get();

        assertThat(userCheck.getName()).isEqualTo("UserNameNew");
    }

    @Test
    void testGetListUsers() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        userDbStorage.save(user);
        User user2 = new User();
        user2.setName("UserName2");
        user2.setLogin("login2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(1994, 03, 9));
        userDbStorage.save(user2);
        List<User> users = userDbStorage.getList();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    void testGetByIdUser() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long id = userDB.getId();
        User userCheck = userDbStorage.getById(id).get();
        assertThat(userCheck.getName()).isEqualTo("UserName");
        assertThat(userCheck.getLogin()).isEqualTo("login");
        assertThat(userCheck.getEmail()).isEqualTo("test@test.ru");
    }

    @Test
    void testAddFriendDeleteFriend() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long id1 = userDB.getId();
        User user2 = new User();
        user2.setName("UserName2");
        user2.setLogin("login2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(1994, 03, 9));
        User userDB2 = userDbStorage.save(user2);
        Long id2 = userDB2.getId();
        friendshipDbStorage.addFriend(id1, id2);
        List<User> friends = friendshipDbStorage.getFriends(id1);
        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.contains(userDB2)).isTrue();
        friendshipDbStorage.deleteFriend(id1, id2);
        List<User> friendsDelete = friendshipDbStorage.getFriends(id1);
        assertThat(friendsDelete.size()).isEqualTo(0);
    }

    @Test
    void testCommonFriends() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long id1 = userDB.getId();
        User user2 = new User();
        user2.setName("UserName2");
        user2.setLogin("login2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(1994, 03, 9));
        User userDB2 = userDbStorage.save(user2);
        Long id2 = userDB2.getId();
        User user3 = new User();
        user3.setName("UserName3");
        user3.setLogin("login3");
        user3.setEmail("test3@test.ru");
        user3.setBirthday(LocalDate.of(1995, 03, 9));
        User userDB3 = userDbStorage.save(user3);
        Long id3 = userDB3.getId();

        friendshipDbStorage.addFriend(id1, id3);
        friendshipDbStorage.addFriend(id2, id3);
        List<User> friends = friendshipDbStorage.getCommonFriends(id1, id2);
        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.contains(userDB3)).isTrue();
    }

    @Test
    void testCommonFilms() {
        User user2 = new User();
        user2.setName("UserName2");
        user2.setLogin("login2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB2 = userDbStorage.save(user2);

        User user3 = new User();
        user3.setName("UserName3");
        user3.setLogin("login3");
        user3.setEmail("test3@test.ru");
        user3.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB3 = userDbStorage.save(user3);

        User user4 = new User();
        user4.setName("UserName4");
        user4.setLogin("login4");
        user4.setEmail("test4@test.ru");
        user4.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB4 = userDbStorage.save(user4);

        Rating mpa = new Rating(2, "PG");
        Film film1 = new Film();
        film1.setName("FilmName1");
        film1.setDescription("Description1");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(2010, 02, 9));
        film1.setMpa(mpa);
        Film filmDB1 = filmDbStorage.save(film1);

        Rating mpa2 = new Rating(1, "G");
        Film film2 = new Film();
        film2.setName("FilmName2");
        film2.setDescription("Description2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(2010, 02, 9));
        film2.setMpa(mpa2);
        Film filmDB2 = filmDbStorage.save(film2);

        Film film3 = new Film();
        film3.setName("FilmName3");
        film3.setDescription("Description3");
        film3.setDuration(120);
        film3.setReleaseDate(LocalDate.of(2010, 02, 9));
        film3.setMpa(mpa);
        Film filmDB3 = filmDbStorage.save(film3);

        likeDbStorage.addLike(filmDB1.getId(), userDB2.getId());
        likeDbStorage.addLike(filmDB1.getId(), userDB3.getId());
        likeDbStorage.addLike(filmDB1.getId(), userDB4.getId());

        likeDbStorage.addLike(filmDB2.getId(), userDB3.getId());

        likeDbStorage.addLike(filmDB3.getId(), userDB2.getId());
        likeDbStorage.addLike(filmDB3.getId(), userDB4.getId());

        List<Film> commonFilms = likeDbStorage.getCommonFilms(userDB2.getId(), userDB4.getId());
        assertThat(commonFilms.size()).isEqualTo(2);
        assertThat(commonFilms.contains(filmDB1)).isTrue();
        assertThat(commonFilms.contains(filmDB3)).isTrue();
        assertThat((commonFilms).get(0).getName()).isEqualTo("FilmName1");
    }

    @Test
    void testSaveFilmDeleteFilm() {
        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 02, 9));
        film.setMpa(mpa);
        Film filmDB = filmDbStorage.save(film);
        Long id = filmDB.getId();
        Film filmCheck = filmDbStorage.getById(id).get();
        assertThat(filmCheck.getName()).isEqualTo("FilmName");
        assertThat(filmCheck.getDescription()).isEqualTo("Description");
        assertThat(filmCheck.getDuration()).isEqualTo(100);
        assertThat(filmCheck.getMpa()).isEqualTo(mpa);

        filmDbStorage.delete(id);
        Optional<Film> filmDelete = filmDbStorage.getById(id);
        assertThat(filmDelete.isEmpty()).isTrue();
    }

    @Test
    void testSaveUserDeleteUser() {
        User user = new User();
        user.setName("UserName");
        user.setLogin("login");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB = userDbStorage.save(user);
        Long id = userDB.getId();
        User userCheck = userDbStorage.getById(id).get();
        assertThat(userCheck.getName()).isEqualTo("UserName");
        assertThat(userCheck.getLogin()).isEqualTo("login");
        assertThat(userCheck.getEmail()).isEqualTo("test@test.ru");

        userDbStorage.delete(id);
        Optional<User> userDelete = userDbStorage.getById(id);
        assertThat(userDelete.isEmpty()).isTrue();
    }

    @Test
    void testDeleteRatingSaveRating() {
        long id = 1L;
        ratingDbStorage.delete(id);
        Optional<Rating> ratingDelete = ratingDbStorage.getById(id);
        assertThat(ratingDelete.isEmpty()).isTrue();

        Rating ratingSave = new Rating(id, "G");
        ratingDbStorage.save(ratingSave);
        Optional<Rating> ratingCheck = ratingDbStorage.getById(id);
        assertThat(ratingCheck.get().getName()).isEqualTo("G");
    }

    @Test
    void testDeleteGenreSaveGenre() {
        Long id = 1L;
        genreDbStorage.delete(id);
        Optional<Genre> genreDelete = genreDbStorage.getById(id);
        assertThat(genreDelete.isEmpty()).isTrue();

        Genre genreSave = new Genre(id.intValue(), "Комедия");
        genreDbStorage.save(genreSave);
        Optional<Genre> genreCheck = genreDbStorage.getById(id);
        assertThat(genreCheck.get().getName()).isEqualTo("Комедия");
    }
}