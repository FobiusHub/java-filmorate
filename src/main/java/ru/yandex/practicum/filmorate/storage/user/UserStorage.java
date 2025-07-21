package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    void update(User user);

    void delete(long id);

    User get(long id);

    List<User> getAll();

    boolean exists(long id);
}
