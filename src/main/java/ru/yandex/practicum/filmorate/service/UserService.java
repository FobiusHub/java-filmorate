package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        validateUserData(user);
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
        validateUserData(newUserData);
        userStorage.update(newUserData);
    }

    public List<User> findAll() {
        return userStorage.getAll();
    }

    public User getUser(long id) {
        validateUserExists(id);
        return userStorage.get(id);
    }

    public void addFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        User user = userStorage.get(userId);
        User anotherUser = userStorage.get(friendId);

        user.addFriend(friendId);
        anotherUser.addFriend(userId);
    }

    public void removeFriend(long userId, long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        User user = userStorage.get(userId);
        User anotherUser = userStorage.get(friendId);

        user.removeFriend(friendId);
        anotherUser.removeFriend(userId);
    }

    public List<User> getFriends(long id) {
        validateUserExists(id);
        return userStorage.get(id).getFriends().stream()
                .map(userStorage::get)
                .toList();
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        validateUserExists(userId);
        validateUserExists(otherId);

        List<Long> userFriends = userStorage.get(userId).getFriends();
        List<Long> anotherUserFriends = userStorage.get(otherId).getFriends();

        return userFriends.stream()
                .filter(anotherUserFriends::contains)
                .map(userStorage::get)
                .toList();
    }

    private void validateUserData(User user) {
        String login = user.getLogin();
        if (login.contains(" ") || login.isBlank()) {
            log.warn("При добавлении пользователя возникла ошибка: Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    public void validateUserExists(long id) {
        if (!userStorage.exists(id)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
    }
}
