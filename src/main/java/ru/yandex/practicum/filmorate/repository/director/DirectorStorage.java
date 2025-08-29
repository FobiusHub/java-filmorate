package ru.yandex.practicum.filmorate.repository.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director get(long id);

    List<Director> getAll();

    Director add(Director director);

    Director update(Director director);

    void delete(long id);

    boolean exists(long id);
}
