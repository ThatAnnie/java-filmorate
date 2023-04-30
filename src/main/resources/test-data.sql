MERGE INTO genres (genre_id, name)
values (1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO rating (rating_id, name)
values (1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, rating_id) VALUES ('Фильма111', 'Описание111', '2000-01-01', 100, 1);
INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, rating_id) VALUES ('НовыйПродукт', 'Описание222', '2015-11-11', 150, 2);

INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES ('1@ya.ru', 'логин', 'имя', '2000-01-01');
INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES ('2@ya.ru', 'логин22', 'имя22', '2002-01-01');

INSERT INTO film_like (FILM_ID, USER_ID) VALUES (2, 1);
INSERT INTO film_like (FILM_ID, USER_ID) VALUES (2, 2);

INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (1, 1);
INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (1, 2);
INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (2, 2);

INSERT INTO DIRECTORS (DIR_NAME) 
VALUES ('Иванов'), 
('ИльфПетров');

INSERT INTO film_director (FILM_ID, DIRECTOR_ID) VALUES (1, 1);
INSERT INTO film_director (FILM_ID, DIRECTOR_ID) VALUES (2, 2);