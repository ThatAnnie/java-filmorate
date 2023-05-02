DROP TABLE IF EXISTS films, film_genre, genres, rating, users, friendship, film_like, film_director, directors;

CREATE TABLE rating (
    rating_id integer   GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar   NOT NULL,
	CONSTRAINT uc_rating_name UNIQUE (name)
);

CREATE TABLE genres (
    genre_id integer   GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar   NOT NULL,
    CONSTRAINT uc_genres_name UNIQUE (name)
);

CREATE TABLE films (
    film_id bigint   GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar   NOT NULL,
    description varchar   NOT NULL,
    duration integer   NOT NULL,
    release_date date   NOT NULL,
    rating_id integer   NOT NULL,
	CONSTRAINT fk_films_rating_id FOREIGN KEY(rating_id) REFERENCES rating (rating_id)
);

CREATE TABLE film_genre (
    film_id bigint   NOT NULL,
    genre_id integer   NOT NULL,
    CONSTRAINT pk_film_genre PRIMARY KEY (
        film_id,genre_id
     ),
	CONSTRAINT fk_film_genre_film_id FOREIGN KEY(film_id) REFERENCES films (film_id) ON DELETE CASCADE,
	CONSTRAINT fk_film_genre_genre_id FOREIGN KEY(genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE
);

CREATE TABLE users (
    user_id bigint   GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar   NOT NULL,
    email varchar   NOT NULL,
    login varchar   NOT NULL,
    birthday date   NOT NULL,
	CONSTRAINT uc_users_email UNIQUE (email),
    CONSTRAINT uc_users_login UNIQUE (login)
);
CREATE UNIQUE index if NOT EXISTS user_email_uindex ON users (email);
CREATE UNIQUE index if NOT EXISTS user_login_uindex ON users (login);

CREATE TABLE friendship (
    user_id bigint   NOT NULL,
    friend_id bigint   NOT NULL,
    CONSTRAINT pk_friendship PRIMARY KEY (
        user_id,friend_id
     ),
	CONSTRAINT fk_friendship_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE,
	CONSTRAINT fk_friendship_friend_id FOREIGN KEY(friend_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE film_like (
    film_id bigint   NOT NULL,
    user_id bigint   NOT NULL,
    CONSTRAINT pk_film_like PRIMARY KEY (
        film_id,user_id
     ),
	CONSTRAINT fk_film_like_film_id FOREIGN KEY(film_id) REFERENCES films (film_id) ON DELETE CASCADE,
	CONSTRAINT fk_film_like_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors (
director_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
dir_name varchar(120) NOT NULL,
CONSTRAINT directors_cs UNIQUE (dir_name)
);

CREATE TABLE IF NOT EXISTS film_director (
film_id BIGINT NOT NULL,
director_id BIGINT NOT NULL,
 CONSTRAINT pk_film_director PRIMARY KEY (
        film_id,director_id
     ),
CONSTRAINT fk_film_director_film_id FOREIGN KEY(film_id) REFERENCES films (film_id) ON DELETE CASCADE,
CONSTRAINT fk_film_director_director_id FOREIGN KEY(director_id) REFERENCES directors (director_id) ON DELETE CASCADE
);