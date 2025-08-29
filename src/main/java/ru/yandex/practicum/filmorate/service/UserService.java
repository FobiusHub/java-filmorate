package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.event.EventStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final FilmStorage filmStorage;

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
        checkUserExist(userId);
        checkUserExist(friendId);
        userStorage.addFriend(userId, friendId);
        eventStorage.add(new Event(userId, EventType.FRIEND, Operation.ADD, friendId));
    }

    public void removeFriend(long userId, long friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);
        userStorage.removeFriend(userId, friendId);
        eventStorage.add(new Event(userId, EventType.FRIEND, Operation.REMOVE, friendId));
    }

    public List<User> getFriends(long userId) {
        checkUserExist(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        checkUserExist(userId);
        checkUserExist(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    public void deleteUser(long id) {
        checkUserExist(id);
        userStorage.delete(id);
    }

    public List<Event> getEvents(long userId) {
        checkUserExist(userId);
        return eventStorage.getFeed(userId);
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

    private void checkUserExist(long userId) {
        if (!userStorage.exists(userId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
    }
}
