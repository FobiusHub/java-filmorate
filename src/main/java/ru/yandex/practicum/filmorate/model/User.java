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
    @NotNull(message = "Некорректный email")
    private String email;
    @NotNull(message = "Логин - обязательное поле")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
