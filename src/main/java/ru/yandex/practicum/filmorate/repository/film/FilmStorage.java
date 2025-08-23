package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    void update(Film film);

    void delete(long id);

    Film get(long id);

    List<Film> getAll();

    boolean exists(long id);

    void like(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getTopFilms(long size);

    List<Film> getDirectorFilmsSortedByLikes(long directorId);

    List<Film> getDirectorFilmsSortedByYear(long directorId);
}
