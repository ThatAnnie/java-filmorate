DROP TABLE IF EXISTS films, film_genre, genres, rating, users, friendship, film_like, reviews, useful;

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
	CONSTRAINT fk_film_genre_film_id FOREIGN KEY(film_id) REFERENCES films (film_id),
	CONSTRAINT fk_film_genre_genre_id FOREIGN KEY(genre_id) REFERENCES genres (genre_id)
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
	CONSTRAINT fk_film_like_user_id FOREIGN KEY(user_id) REFERENCES users (user_id)
);

CREATE TABLE reviews (
    review_id bigint   GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content varchar   NOT NULL,
    is_Positive boolean NOT NULL,
    user_id bigint   NOT NULL,
    film_id bigint   NOT NULL,
    useful bigint   DEFAULT 0,
    CONSTRAINT fk_reviews_film_id FOREIGN KEY(film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user_id FOREIGN KEY(user_id) REFERENCES users (user_id)
);

CREATE TABLE useful (
    review_id bigint   NOT NULL,
    user_id bigint   NOT NULL,
    useful_estimation boolean NOT NULL,
    CONSTRAINT pk_reviews_like PRIMARY KEY (
        review_id,user_id
     ),
	CONSTRAINT fk_useful_review_id FOREIGN KEY(review_id) REFERENCES reviews (review_id) ON DELETE CASCADE,
	CONSTRAINT fk_useful_user_id FOREIGN KEY(user_id) REFERENCES users (user_id) ON DELETE CASCADE
);