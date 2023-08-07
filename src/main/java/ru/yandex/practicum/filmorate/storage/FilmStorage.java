package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film getFilm(int filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);
}


