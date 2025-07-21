package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(id);
        log.debug("Фильму {} присвоен id {}", film.getName(), id);
        id++;
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void update(Film newFilmData) {
        films.put(newFilmData.getId(), newFilmData);
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    @Override
    public Film get(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean exists(long id) {
        return films.containsKey(id);
    }

}
