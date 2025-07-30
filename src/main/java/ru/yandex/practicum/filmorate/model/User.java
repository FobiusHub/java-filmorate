package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"id", "login"})
@NoArgsConstructor
public class User {
    private Long id;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Некорректный email")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[^ ]*$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Необходимо указать дату рождения")
    private LocalDate birthday;
    private final Map<Long, Boolean> friends = new HashMap<>();

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
