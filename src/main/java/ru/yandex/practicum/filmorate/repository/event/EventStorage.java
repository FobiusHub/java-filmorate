package ru.yandex.practicum.filmorate.repository.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void add(Event event);

    List<Event> getFeed(long userId);
}
