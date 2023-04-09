# java-filmorate
## Диаграмма базы данных
![Database Diagram](/QuickDBD-filmorate.png)

###Описание ER-диаграммы:
Данные пользователей хранятся в таблице **user**, данные о фильмах - в таблице **film**.
В отдельные таблицы вынесены рейтинг фильма(**rating**) и жанр фильма(**genre**).
В таблице **film_like** связывается пользователь с понравившемся ему фильмом (каждая пара уникальна).
В таблицу **film_genre** вынесена принадлежность фильма к определенному жанру(каждая пара уникальна).
В таблице **friendship** хранится информация о запросах пользователей на дружбу, поле is_confirmed хранит статус подтверждения дружбы.

#### Примеры запросов к БД

Получение всех пользователей:
`SELECT * FROM user;`

Получения пользователя по id:
`SELECT * FROM user WHERE user_id = <id>;`

Получение друзей пользователя по id*:
`SELECT friend_id FROM friendship WHERE user_id = <id> AND is_confirmed = TRUE;`

Получения общих друзей пользователей*:
`SELECT f.friend_id FROM friendship f1 INNER JOIN friendship f2 ON f1.friend_id = f2.friend_id 
WHERE f1.user_id = <id1> AND f1.is_confirmed = TRUE AND f2.user_id = <id2> AND f2.is_confirmed = TRUE;`

\* Пользователь считается другом, если он подтвержден.

Получение всех фильмов:
`SELECT * FROM film;`

Получения фильма по id:
`SELECT * FROM film WHERE film_id = <id>;`

Получение топ N наиболее популярных фильмов:
`SELECT f.film_id, COUNT(fl.user_id) count_likes FROM film_like fl LEFT JOIN film f ON fl.film_id = f.film_id 
GROUP BY fl.film_id ORDER BY count_likes DESC LIMIT N;`
