package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"login"})
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
}
