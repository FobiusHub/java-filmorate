package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Review {
    private Long reviewId;
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull(message = "Необходимо указать id пользователя")
    private Long userId;
    @NotNull(message = "Необходимо указать id фильма")
    private Long filmId;
    private long useful;

    public void like() {
        useful++;
    }

    public void dislike() {
        useful--;
    }

    public void removeLike() {
        useful--;
    }

    public void removeDislike() {
        useful++;
    }
}
