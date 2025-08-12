CREATE TABLE if not EXISTS users (
	user_id BIGINT GENERATED ALWAYS AS IDENTITY, -- это поле всегда автомматически присваивается базой данных
	email VARCHAR(200) NOT NULL UNIQUE,
	login VARCHAR(200) NOT NULL UNIQUE,
	name VARCHAR(200) NOT NULL,
	birthday DATE NOT NULL,
	PRIMARY KEY (user_id) -- для H2 первичный ключ объявляем отдельной строкой
);

CREATE TABLE if not EXISTS friends (
-- если удаляется user с указанным user_id, то удаляется и запись в friends
	user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
	friend_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
	PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE if not EXISTS mpa (
	mpa_id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL UNIQUE,
	PRIMARY KEY (mpa_id)
);

CREATE TABLE if not EXISTS films (
	film_id BIGINT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL,
	description VARCHAR(200) NOT NULL,
	release_date DATE NOT NULL CHECK (release_date >= '1895-12-28'),
	duration INT CHECK (duration > 0),
	mpa_id INT REFERENCES mpa(mpa_id),
	PRIMARY KEY (film_id)
);

CREATE TABLE if not EXISTS likes (
	film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
	user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
	PRIMARY KEY (film_id, user_id)
);

CREATE TABLE if not EXISTS genres (
	genre_id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL UNIQUE,
	PRIMARY KEY (genre_id)
);

CREATE TABLE if not EXISTS film_genres (
	film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
	genre_id INT REFERENCES genres(genre_id) ON DELETE CASCADE,
	PRIMARY KEY (film_id, genre_id)
);