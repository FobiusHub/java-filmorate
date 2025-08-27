package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    void update(User user);

    void delete(long id);

    User get(long id);

    List<User> getAll();

    boolean exists(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long userId, long otherId);

    public List<Long> getUsersIdWithSimilarLikes(long id);

}
