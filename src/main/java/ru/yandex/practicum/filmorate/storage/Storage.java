package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    List<T> getAll();

    T searchById(int idT);

    T create(T t);

    T update(T t);

    boolean checkExistence(int idT);
}
