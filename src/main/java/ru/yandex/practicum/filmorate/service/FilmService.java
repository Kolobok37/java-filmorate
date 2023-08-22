package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.StorageFilm;
import ru.yandex.practicum.filmorate.storage.StorageUser;

import java.util.List;

@Service
public class FilmService {
    @Autowired
    @Qualifier("filmDbStorage")
    private StorageFilm filmStorage;
    @Autowired
    @Qualifier("userDbStorage")
    private StorageUser userStorage;

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film getFilm(int filmId) {
        checkExistenceFilm(filmId);
        return filmStorage.searchById(filmId);
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getPopularFilm(count);
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        checkExistenceFilm(film.getId());
        return filmStorage.update(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        checkExistenceFilm(filmId);
        userStorage.checkExistence(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        checkExistenceFilm(filmId);
        checkExistenceUser(userId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Пользователь {} удалил лайк фильму %d", userId, filmId);
    }

    private void checkExistenceFilm(int filmId) {
        if (!filmStorage.checkExistence(filmId)) {
            log.warn("Ошибка: фильм {} не найден", filmId);
            throw new NotFoundException("Фильм не найден.");
        }
    }

    private void checkExistenceUser(int id) {
        if (!userStorage.checkExistence(id)) {
            log.warn("Ошибка: пользователь {} не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }
}
