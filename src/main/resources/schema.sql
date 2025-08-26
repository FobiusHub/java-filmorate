create TABLE IF NOT EXISTS users (
	user_id BIGINT GENERATED ALWAYS AS IDENTITY, -- это поле всегда автоматически присваивается базой данных
	email VARCHAR(200) NOT NULL UNIQUE,
	login VARCHAR(200) NOT NULL UNIQUE,
	name VARCHAR(200) NOT NULL,
	birthday DATE NOT NULL,
	PRIMARY KEY (user_id) -- для H2 первичный ключ объявляем отдельной строкой
);

create TABLE IF NOT EXISTS friends (
-- если удаляется user с указанным user_id, то удаляется и запись в friends
	user_id BIGINT REFERENCES users(user_id) ON delete CASCADE,
	friend_id BIGINT REFERENCES users(user_id) ON delete CASCADE,
	PRIMARY KEY (user_id, friend_id)
);

create TABLE IF NOT EXISTS mpa (
	mpa_id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL UNIQUE,
	PRIMARY KEY (mpa_id)
);

create TABLE IF NOT EXISTS films (
	film_id BIGINT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL,
	description VARCHAR(200) NOT NULL,
	release_date DATE NOT NULL CHECK (release_date >= '1895-12-28'),
	duration INT CHECK (duration > 0),
	mpa_id INT REFERENCES mpa(mpa_id),
	PRIMARY KEY (film_id)
);

create TABLE IF NOT EXISTS likes (
	film_id BIGINT REFERENCES films(film_id) ON delete CASCADE,
	user_id BIGINT REFERENCES users(user_id) ON delete CASCADE,
	PRIMARY KEY (film_id, user_id)
);

create TABLE IF NOT EXISTS genres (
	genre_id INT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL UNIQUE,
	PRIMARY KEY (genre_id)
);

create TABLE IF NOT EXISTS film_genres (
	film_id BIGINT REFERENCES films(film_id) ON delete CASCADE,
	genre_id INT REFERENCES genres(genre_id) ON delete CASCADE,
	PRIMARY KEY (film_id, genre_id)
);

create TABLE IF NOT EXISTS reviews (
    review_id BIGINT GENERATED ALWAYS AS IDENTITY,
    content VARCHAR(1500) NOT NULL,
    positive BOOL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    useful BIGINT,
    PRIMARY KEY (review_id)
);

create TABLE if not EXISTS directors (
	director_id BIGINT GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(200) NOT NULL UNIQUE,
	PRIMARY KEY (director_id)
);

create TABLE if not EXISTS film_directors (
	film_id BIGINT REFERENCES films(film_id) ON delete CASCADE,
	director_id BIGINT REFERENCES directors(director_id) ON delete CASCADE,
	PRIMARY KEY (film_id, director_id)
);

CREATE TABLE if not EXISTS events (
    event_id BIGINT GENERATED ALWAYS AS IDENTITY,
    timestamp BIGINT NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    event_type VARCHAR(6) NOT NULL,
    operation VARCHAR(6) NOT NULL,
    entity_id BIGINT NOT NULL,
    PRIMARY KEY (event_id)
);