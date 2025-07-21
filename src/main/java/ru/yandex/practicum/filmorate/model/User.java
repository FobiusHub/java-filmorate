package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"id", "login"})
@NoArgsConstructor
public class User {
    private Long id;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Некорректный email")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Необходимо указать дату рождения")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(long friendId) {
        friends.remove(friendId);
    }

    public List<Long> getFriends() {
        return new ArrayList<>(friends);
    }
}
