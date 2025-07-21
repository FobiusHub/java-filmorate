package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    void update(Film film);

    void delete(long id);

    Film get(long id);

    List<Film> getAll();

    boolean exists(long id);
}
