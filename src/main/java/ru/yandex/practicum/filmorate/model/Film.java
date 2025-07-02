package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"name", "releaseDate", "duration"})
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания - 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Дата релиза должна быть указана")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;
}
