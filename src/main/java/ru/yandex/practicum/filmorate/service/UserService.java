package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userDb") UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User create(User user) {
        checkName(user);
        return userStorage.add(user);
    }

    public void update(User newUserData) {
        Long newUserDataId = newUserData.getId();
        if (newUserDataId == null) {
            log.warn("При обновлении данных пользователя возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!userStorage.exists(newUserDataId)) {
            log.warn("При обновлении данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + newUserDataId + " не найден");
        }
        checkName(newUserData);
        userStorage.update(newUserData);
    }

    public List<User> findAll() {
        return userStorage.getAll();
    }

    public User getUser(long id) {
        return userStorage.get(id);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        if (!userStorage.exists(friendId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + friendId + " не найден");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        if (!userStorage.exists(friendId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + friendId + " не найден");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long id) {
        if (!userStorage.exists(id)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        if (!userStorage.exists(userId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        if (!userStorage.exists(otherId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + otherId + " не найден");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }

    public User deleteUser(long id) {
        User user = userStorage.get(id);
        userStorage.delete(id);
        return user;
    }

    public List<Film> getRecommendations(long id) {
        List<Long> usersIdWithSimilarLikes = userStorage.getUsersIdWithSimilarLikes(id);
        if (usersIdWithSimilarLikes.isEmpty()) {
            return Collections.emptyList();
        }
        return filmStorage.getRecommendationFilms(usersIdWithSimilarLikes, id);
    }

    private void checkName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя не указано. В качестве имени присвоен логин {}.", login);
        }
    }
}
