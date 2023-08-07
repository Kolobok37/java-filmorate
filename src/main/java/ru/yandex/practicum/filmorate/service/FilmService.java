package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getFilms().stream().sorted(Comparator.comparing(Film::getNumberLike)).
                limit(count).collect(Collectors.toList());
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.getFilms().get(filmId).addLike(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.getFilms().get(filmId).deleteLike(userId);
    }
}
